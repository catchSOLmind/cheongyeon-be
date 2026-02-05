package com.catchsolmind.cheongyeonbe.domain.eraser.service;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.request.ReservationRequest;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.RecommendationResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.Reservation;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.SuggestionTask;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.SuggestionTaskOption;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.ReservationRepository;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.SuggestionTaskOptionRepository;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.SuggestionTaskRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskLogRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.config.S3Properties;
import com.catchsolmind.cheongyeonbe.global.enums.SuggestionType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EraserServiceTest {
    @InjectMocks
    private EraserService eraserService;

    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private SuggestionTaskRepository suggestionTaskRepository;
    @Mock
    private TaskOccurrenceRepository taskOccurrenceRepository;
    @Mock
    private TaskLogRepository taskLogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SuggestionTaskOptionRepository suggestionTaskOptionRepository;
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private S3Properties s3Properties;

    @Test
    @DisplayName("상황 1: 일정에 있는데 3번 이상 미뤘으면 추천 목록에 떠야 한다.")
    void recommendWhenPostponed3Times() {
        Long userId = 1L;
        Long optionId = 100L;
        Long groupId = 10L;
        int optionPrice = 30000;
        int usedPoint = 5000;
        int userBalance = 50000;

        User user = User.builder().userId(userId).pointBalance(userBalance).build();
        Group group = Group.builder().groupId(groupId).build();

        given(userRepository.findByIdWithPessimisticLock(userId)).willReturn(Optional.of(user));
        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));

        // [Fix 2] TaskType -> SuggestionTask -> Option 순으로 객체 연결 (NPE 해결)
        TaskType taskType = TaskType.builder().taskTypeId(55L).build();

        SuggestionTask task = SuggestionTask.builder()
                .suggestionTaskId(1L)
                .title("청소")
                .taskType(taskType) // TaskType 연결 필수!
                .build();

        SuggestionTaskOption option = SuggestionTaskOption.builder()
                .optionId(optionId)
                .price(optionPrice)
                .count("1개")
                .suggestionTask(task) // Task 연결 필수!
                .build();

        given(suggestionTaskOptionRepository.findById(optionId)).willReturn(Optional.of(option));

        TaskOccurrence existingTask = TaskOccurrence.builder()
                .status(TaskStatus.WAITING)
                .build();
        given(taskOccurrenceRepository.findByGroupAndTaskTypeAndStatusIn(eq(groupId), eq(55L), anyList()))
                .willReturn(List.of(existingTask));

        // [Fix 3] save() 호출 시 ID가 있는 객체를 리턴하도록 설정
        Reservation savedReservation = Reservation.builder().reservationId(999L).build();
        given(reservationRepository.save(any(Reservation.class))).willReturn(savedReservation);

        ReservationRequest request = ReservationRequest.builder()
                .usedPoint(usedPoint)
                .reservations(List.of(
                        ReservationRequest.ReservationItemRequest.builder()
                                .optionId(optionId)
                                .visitDate(LocalDate.now().plusDays(1))
                                .visitTime("14:00")
                                .build()
                ))
                .build();

        // when
        Long reservationId = eraserService.completeReservation(request, userId);

        // then
        assertThat(reservationId).isEqualTo(999L);
        assertThat(user.getPointBalance()).isEqualTo(userBalance - usedPoint);
        assertThat(existingTask.getStatus()).isEqualTo(TaskStatus.RESOLVED_BY_ERASER);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("상황 2: 일정에 없지만, 마지막으로 한 지 주기가 지났으면 추천에 떠야 한다.")
    void recommendWhenCyclePassed() {
        // given
        Long userId = 1L;
        Group group = Group.builder().groupId(100L).build();
        TaskType taskType = TaskType.builder().taskTypeId(2L).build();

        // [Fix] S3 Properties Stubbing 추가 (이 줄이 없어서 에러 발생)
        given(s3Properties.getBaseUrl()).willReturn("https://test-s3-url.com");

        // 고정된 상품 데이터
        SuggestionTask product = SuggestionTask.builder()
                .suggestionTaskId(2L)
                .taskType(taskType)
                .title("화장실 청소")
                .imgUrl("restroom.png") // 이미지 URL 생성 테스트를 위해 필요
                .defaultEstimatedMinutes(60)
                .recommendationCycleDays(7)
                .descNoAssignee("{task_name} 담당자가 없네요.")
                .build();

        LocalDateTime lastDoneDate = LocalDateTime.now().minusDays(10);
        Object[] logRow = {2L, lastDoneDate};

        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));
        given(suggestionTaskRepository.findAll()).willReturn(List.of(product));
        given(taskOccurrenceRepository.findUnfinishedByGroupId(anyLong())).willReturn(List.of());
        given(taskLogRepository.findLastDoneDatesByGroupAndTaskTypes(anyLong(), anyList()))
                .willReturn(Collections.singletonList(logRow));

        // when
        List<RecommendationResponse> results = eraserService.getRecommendations(userId);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("화장실 청소");
        assertThat(results.get(0).imgUrl()).isEqualTo("https://test-s3-url.com/restroom.png"); // URL 조립 검증
        assertThat(results.get(0).tags()).contains(SuggestionType.NO_ASSIGNEE);
    }

    @Test
    @DisplayName("상황 3: 일정에 없고, 주기도 아직 안 지났으면 추천 안 함")
    void doNotRecommendWhenCycleNotPassed() {
        // given
        Long userId = 1L;
        Group group = Group.builder().groupId(100L).build();
        TaskType taskType = TaskType.builder().taskTypeId(2L).build();

        // 고정된 상품 데이터 (화장실 청소, 주기 7일)
        SuggestionTask product = SuggestionTask.builder()
                .suggestionTaskId(2L)
                .taskType(taskType)
                .defaultEstimatedMinutes(60) // [수정] NPE 방지를 위해 필수값 추가
                .recommendationCycleDays(7)
                .build();

        // 유저의 마지막 기록 (3일 전) -> 아직 주기 안 됨
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        Object[] logRow = {2L, threeDaysAgo};

        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));
        given(suggestionTaskRepository.findAll()).willReturn(List.of(product));
        given(taskOccurrenceRepository.findUnfinishedByGroupId(anyLong())).willReturn(List.of());
        given(taskLogRepository.findLastDoneDatesByGroupAndTaskTypes(anyLong(), anyList()))
                .willReturn(Collections.singletonList(logRow));

        // when
        List<RecommendationResponse> results = eraserService.getRecommendations(userId);

        // then
        assertThat(results).isEmpty(); // 추천 목록이 비어있어야 함
    }

    @Test
    @DisplayName("정상 예약 확정 케이스 검증")
    void completeReservationSuccess() {
        // given
        Long userId = 1L;
        Long optionId = 100L;
        Long groupId = 10L;
        int optionPrice = 30000;
        int usedPoint = 5000;
        int userBalance = 50000;

        // 1. User & Group Mocking
        User user = User.builder().userId(userId).pointBalance(userBalance).build();
        Group group = Group.builder().groupId(groupId).build();

        given(userRepository.findByIdWithPessimisticLock(userId)).willReturn(Optional.of(user));
        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));

        // 2. Option & TaskType Mocking
        TaskType taskType = TaskType.builder().taskTypeId(55L).build();
        SuggestionTask task = SuggestionTask.builder().title("청소").taskType(taskType).build();
        SuggestionTaskOption option = SuggestionTaskOption.builder()
                .optionId(optionId)
                .price(optionPrice)
                .count("1개")
                .suggestionTask(task)
                .build();

        given(suggestionTaskOptionRepository.findById(optionId)).willReturn(Optional.of(option));

        // 3. Eraser Logic (TaskOccurrence) Mocking
        TaskOccurrence existingTask = TaskOccurrence.builder()
                .status(TaskStatus.WAITING)
                .build();
        given(taskOccurrenceRepository.findByGroupAndTaskTypeAndStatusIn(eq(groupId), eq(55L), anyList()))
                .willReturn(List.of(existingTask));

        // 4. Reservation Save Mocking
        Reservation savedReservation = Reservation.builder().reservationId(999L).build();
        given(reservationRepository.save(any(Reservation.class))).willReturn(savedReservation);

        // Request 생성
        ReservationRequest request = ReservationRequest.builder()
                .usedPoint(usedPoint)
                .reservations(List.of(
                        ReservationRequest.ReservationItemRequest.builder()
                                .optionId(optionId)
                                .visitDate(LocalDate.now().plusDays(1))
                                .visitTime("14:00")
                                .build()
                ))
                .build();

        // when
        Long reservationId = eraserService.completeReservation(request, userId);

        // then
        assertThat(reservationId).isEqualTo(999L);
        assertThat(user.getPointBalance()).isEqualTo(userBalance - usedPoint); // 포인트 차감 확인
        assertThat(existingTask.getStatus()).isEqualTo(TaskStatus.RESOLVED_BY_ERASER); // 지우개 로직 확인
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("포인트 부족 예외 검증 (usedPoint > currentPoint)")
    void completeReservation_NotEnoughPoint() {
        // given
        Long userId = 1L;
        int userBalance = 1000;
        int usedPoint = 5000;

        User user = User.builder().userId(userId).pointBalance(userBalance).build();
        Group group = Group.builder().groupId(10L).build();

        given(userRepository.findByIdWithPessimisticLock(userId)).willReturn(Optional.of(user));
        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));

        ReservationRequest request = ReservationRequest.builder()
                .usedPoint(usedPoint)
                .build();

        // when & then
        assertThatThrownBy(() -> eraserService.completeReservation(request, userId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.POINT_NOT_ENOUGH);
    }

    @Test
    @DisplayName("최대 사용 포인트 초과 예외 검증 (usedPoint > MAX_USABLE_POINT)")
    void completeReservation_ExceedMaxPoint() {
        // given
        Long userId = 1L;
        int userBalance = 50000;
        int usedPoint = 20001; // 2만 포인트 초과

        User user = User.builder().userId(userId).pointBalance(userBalance).build();
        Group group = Group.builder().groupId(10L).build();

        given(userRepository.findByIdWithPessimisticLock(userId)).willReturn(Optional.of(user));
        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));

        ReservationRequest request = ReservationRequest.builder()
                .usedPoint(usedPoint)
                .build();

        // when & then
        assertThatThrownBy(() -> eraserService.completeReservation(request, userId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_POINT_AMOUNT);
    }

    @Test
    @DisplayName("옵션 미존재 예외 검증 (OPTION_NOT_FOUND)")
    void completeReservation_OptionNotFound() {
        // given
        Long userId = 1L;
        Long optionId = 999L;

        User user = User.builder().userId(userId).pointBalance(50000).build();
        Group group = Group.builder().groupId(10L).build();

        given(userRepository.findByIdWithPessimisticLock(userId)).willReturn(Optional.of(user));
        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));

        // 옵션 조회 시 Empty 반환
        given(suggestionTaskOptionRepository.findById(optionId)).willReturn(Optional.empty());

        ReservationRequest request = ReservationRequest.builder()
                .usedPoint(0)
                .reservations(List.of(
                        ReservationRequest.ReservationItemRequest.builder()
                                .optionId(optionId)
                                .build()
                ))
                .build();

        // when & then
        assertThatThrownBy(() -> eraserService.completeReservation(request, userId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.OPTION_NOT_FOUND);
    }

    @Test
    @DisplayName("음수 결제 금액 예외 검증 (INVALID_PAYMENT_AMOUNT)")
    void completeReservation_NegativePayment() {
        // given
        Long userId = 1L;
        Long optionId = 100L;
        int optionPrice = 10000;
        int usedPoint = 15000;

        User user = User.builder().userId(userId).pointBalance(50000).build();
        Group group = Group.builder().groupId(10L).build();

        given(userRepository.findByIdWithPessimisticLock(userId)).willReturn(Optional.of(user));
        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));

        // [Fix 2 반복] NPE 방지를 위한 더미 객체 연결
        TaskType taskType = TaskType.builder().taskTypeId(55L).build();
        SuggestionTask task = SuggestionTask.builder().taskType(taskType).build();

        SuggestionTaskOption option = SuggestionTaskOption.builder()
                .optionId(optionId)
                .price(optionPrice)
                .suggestionTask(task) // 연결
                .build();

        given(suggestionTaskOptionRepository.findById(optionId)).willReturn(Optional.of(option));

        ReservationRequest request = ReservationRequest.builder()
                .usedPoint(usedPoint)
                .reservations(List.of(
                        ReservationRequest.ReservationItemRequest.builder()
                                .optionId(optionId)
                                .build()
                ))
                .build();

        // when & then
        assertThatThrownBy(() -> eraserService.completeReservation(request, userId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PAYMENT_AMOUNT);
    }
}
