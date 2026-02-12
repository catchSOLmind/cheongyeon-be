package com.catchsolmind.cheongyeonbe.domain.auth.service;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.Agreement;
import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementItem;
import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementSign;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementItemRepository;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementRepository;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementSignRepository;
import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.GuestLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.auth.entity.RefreshToken;
import com.catchsolmind.cheongyeonbe.domain.auth.repository.RefreshTokenRepository;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.Reservation;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.ReservationItem;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.ReservationRepository;
import com.catchsolmind.cheongyeonbe.domain.feedback.entity.Feedback;
import com.catchsolmind.cheongyeonbe.domain.feedback.entity.ImprovementFeedback;
import com.catchsolmind.cheongyeonbe.domain.feedback.repository.FeedbackRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskLog;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskLogRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTypeRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.enums.*;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final AgreementRepository agreementRepository;
    private final AgreementItemRepository agreementItemRepository;
    private final AgreementSignRepository agreementSignRepository;
    private final ReservationRepository reservationRepository;
    private final FeedbackRepository feedbackRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TaskLogRepository taskLogRepository; // ★ 대시보드 통계용 로그 저장소 추가
    private final JwtProvider jwtProvider;

    @Transactional
    public GuestLoginResponse enterGuestMode() {
        // 유저 생성 (쏠, 몰리, 게스트)
        User sol = createTempUser("쏠", "https://cheongyeon-fe-solmind.s3.ap-northeast-2.amazonaws.com/backend/profile/sol.png", 52000);
        User molly = createTempUser("몰리", "https://cheongyeon-fe-solmind.s3.ap-northeast-2.amazonaws.com/backend/profile/molly.png", 38000);
        User guest = createTempUser("게스트", "https://cheongyeon-fe-solmind.s3.ap-northeast-2.amazonaws.com/backend/profile/default-profile.png", 0);


        // 그룹 생성
        Group group = Group.builder()
                .name("캐치SOL마인드")
                .ownerUser(sol) // 오너는 쏠
                .build();
        groupRepository.save(group);


        // 멤버 연결 (게스트는 JOINED 상태로 시작)
        GroupMember solMember = joinMember(group, sol, MemberRole.OWNER, MemberStatus.AGREED);
        GroupMember mollyMember = joinMember(group, molly, MemberRole.MEMBER, MemberStatus.AGREED);
        GroupMember guestMember = joinMember(group, guest, MemberRole.MEMBER, MemberStatus.AGREED);


        // 협약서
        createSeededAgreement(group, solMember, mollyMember);


        // 가사(Task) 시딩
        createDummyTask(group, mollyMember, 96L, "음식물 쓰레기 버리기", "냄새 나기 전에 제발 버리자", TaskStatus.WAITING, 3, false);
        createDummyTask(group, guestMember, 26L, "설거지", "어제 먹은 야식 그릇 정리", TaskStatus.WAITING, 0, true);
        createDummyTask(group, solMember, 43L, "세탁기 돌리기", "흰 빨래 모아서", TaskStatus.COMPLETED, 0, true);

        // 대시보드 TOP 3 통계용 데이터
        createDashboardLogData(group, solMember, mollyMember, guestMember);

        //매니저 호출 시딩
        createManagerReservation(group, sol);

        // 피드백 시딩
        createDummyFeedbacks(group, solMember, mollyMember, guestMember);

        // 토큰 발급
        String accessToken = jwtProvider.createAccessToken(guest.getUserId());
        String refreshTokenVal = jwtProvider.createRefreshToken(guest.getUserId());

        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshTokenVal)
                .user(guest)
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build());

        return GuestLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenVal)
                .userId(guest.getUserId())
                .nickname(guest.getNickname())
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .memberStatus(guestMember.getStatus().name())
                .build();
    }

    /*
     * 헬퍼 머서드
     * */
    private User createTempUser(String nickname, String profileImg, int point) {
        Long randomProviderId = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
        User user = User.builder()
                .nickname(nickname)
                .provider(AuthProvider.GUEST)
                .providerId(randomProviderId)
                .pointBalance(point)
                .profileImg(profileImg)
                .build();
        return userRepository.save(user);
    }

    private GroupMember joinMember(Group group, User user, MemberRole role, MemberStatus status) {
        GroupMember member = GroupMember.builder()
                .user(user)
                .group(group)
                .role(role)
                .status(status)
                .joinedAt(LocalDateTime.now())
                .agreedAt(status == MemberStatus.AGREED ? LocalDateTime.now() : null)
                .build();
        return groupMemberRepository.save(member);
    }

    private void createSeededAgreement(Group group, GroupMember sol, GroupMember molly) {
        Agreement agreement = Agreement.builder()
                .group(group)
                .title("협약서")
                .houseName("캐치SOL하우스")
                .monthlyGoal("청소 3번 미루는 사람이 오마카세 쏘기")
                .deadline(LocalDate.now().plusMonths(1))
                .status(AgreementStatus.CONFIRMED) // 게스트 동의 대기
                .confirmedAt(LocalDateTime.now().minusDays(1))
                .build();
        agreementRepository.save(agreement);

        List<String> rules = List.of(
                "배달 음식은 땡겨요로 시키기",
                "밤 12시 이후엔 조용히 하기"
        );

        for (int i = 0; i < rules.size(); i++) {
            agreementItemRepository.save(AgreementItem.builder()
                    .agreement(agreement)
                    .itemOrder(i + 1)
                    .itemText(rules.get(i))
                    .build());
        }

        agreementSignRepository.save(AgreementSign.builder().agreement(agreement).member(sol).signedAt(LocalDateTime.now()).build());
        agreementSignRepository.save(AgreementSign.builder().agreement(agreement).member(molly).signedAt(LocalDateTime.now()).build());
    }

    private void createDummyTask(Group group, GroupMember member, Long taskTypeId, String title, String desc, TaskStatus status, int postponeCount, boolean occurToday) {
        TaskType taskType = taskTypeRepository.findById(taskTypeId).orElse(null);
        if (taskType == null) return;

        Task task = Task.builder()
                .group(group)
                .taskType(taskType)
                .title(title)
                .description(desc)
                .creatorMember(member)
                .status(status)
                .time("19:00")
                .build();

        TaskOccurrence occurrence = TaskOccurrence.builder()
                .task(task)
                .group(group)
                .occurDate(LocalDate.now())
                .primaryAssignedMember(member)
                .status(status)
                .postponeCount(postponeCount)
                .takeoverCount(0)
                .build();

        task.getOccurrences().add(occurrence);
        taskRepository.save(task);
    }

    // 대시보드 통계(TOP 3)를 위한 과거 완료 데이터 생성
    private void createDashboardLogData(Group group, GroupMember sol, GroupMember molly, GroupMember guest) {
        createCompletedLogs(group, sol, 26L, "설거지", 5);
        createCompletedLogs(group, molly, 18L, "화장실 청소", 3);
        createCompletedLogs(group, guest, 79L, "청소기 돌리기", 2);
    }

    private void createCompletedLogs(Group group, GroupMember member, Long taskTypeId, String title, int count) {
        TaskType taskType = taskTypeRepository.findById(taskTypeId).orElse(null);
        if (taskType == null) return;

        for (int i = 0; i < count; i++) {
            // Task 생성
            Task task = Task.builder()
                    .group(group)
                    .taskType(taskType)
                    .title(title)
                    .creatorMember(member)
                    .status(TaskStatus.COMPLETED)
                    .time("10:00")
                    .build();

            // Occurrence 생성 (과거 날짜)
            TaskOccurrence occurrence = TaskOccurrence.builder()
                    .task(task)
                    .group(group)
                    .occurDate(LocalDate.now().minusDays(i + 1))
                    .primaryAssignedMember(member)
                    .status(TaskStatus.COMPLETED)
                    .postponeCount(0)
                    .takeoverCount(0)
                    .build();

            task.getOccurrences().add(occurrence);
            taskRepository.save(task);

            TaskLog log = TaskLog.builder()
                    .occurrence(occurrence)
                    .doneByMember(member)
                    .doneAt(LocalDateTime.now().minusDays(i + 1))
                    .build();

            taskLogRepository.save(log);
        }
    }

    private void createManagerReservation(Group group, User user) {
        LocalDate visitDate = LocalDate.now().plusDays(1);
        int price = 45000;

        Reservation reservation = Reservation.builder()
                .user(user)
                .totalPrice(price)
                .usedPoint(0)
                .finalPrice(price)
                .status(TaskStatus.RESOLVED_BY_ERASER)
                .build();

        ReservationItem item = ReservationItem.builder()
                .suggestionTaskId(1L)
                .taskTitle("화장실 청소")
                .optionCount("1개")
                .price(price)
                .visitDate(visitDate)
                .visitTime("14:00")
                .rewardPoint(120)
                .build();

        reservation.addReservationItem(item);
        reservationRepository.save(reservation);
    }

    private void createDummyFeedbacks(Group group, GroupMember sol, GroupMember molly, GroupMember guest) {
        // 쏠 -> 몰리 (칭찬 + 개선)
        ImprovementFeedback imp1 = ImprovementFeedback.builder()
                .category(TaskCategory.KITCHEN)
                .rawText("설거지할 때 배수구망도 같이 비워주면 좋겠어!")
                .aiText("설거지하실 때 배수구망도 함께 정리해주시면 더욱 쾌적한 주방이 될 것 같아요! ✨")
                .aiStatus(AiStatus.COMPLETED)
                .build();

        Feedback feedback1 = Feedback.builder()
                .group(group)
                .author(sol)
                .target(molly)
                .praiseTypes(List.of(PraiseType.DETAIL_KING, PraiseType.ORGANIZING_KING))
                .improvements(List.of(imp1))
                .build();

        feedbackRepository.save(feedback1);

        // 몰리 -> 쏠 (칭찬)
        Feedback feedback2 = Feedback.builder()
                .group(group)
                .author(molly)
                .target(sol)
                .praiseTypes(List.of(PraiseType.SCENT_KING))
                .improvements(List.of())
                .build();

        feedbackRepository.save(feedback2);

        // 쏠 -> 게스트 (칭찬)
        Feedback feedback3 = Feedback.builder()
                .group(group)
                .author(sol)
                .target(guest)
                .praiseTypes(List.of(PraiseType.POINT_KING))
                .improvements(List.of())
                .build();

        feedbackRepository.save(feedback3);
    }
}