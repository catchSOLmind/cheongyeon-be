package com.catchsolmind.cheongyeonbe.domain.feedback.service;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.FeedbackCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.GroupMemberWithTestResult;
import com.catchsolmind.cheongyeonbe.domain.feedback.entity.Feedback;
import com.catchsolmind.cheongyeonbe.domain.feedback.repository.FeedbackRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestResultRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.enums.PraiseType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {
    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private HouseworkTestResultRepository houseworkTestResultRepository;
    @Mock
    private FeedbackRepository feedbackRepository;

    @Test
    @DisplayName("피드백 폼 조회 성공 - 본인을 제외한 그룹 멤버와 성향, 칭찬 스티커, 카테고리를 반환한다")
    void getFeedbackSuccess() {
        // given
        Long myUserId = 1L;
        Long otherUserId = 2L;
        Long groupId = 10L;

        // 1. User Mocking
        User me = createUser(myUserId, "나");
        User other = createUser(otherUserId, "멤버");

        // 2. Group & GroupMember Mocking
        Group group = Group.builder().groupId(groupId).build();

        GroupMember myMemberInfo = createGroupMember(100L, me, group);
        GroupMember otherMemberInfo = createGroupMember(200L, other, group);

        // 3. Test Result Mocking
        HouseworkTestResult otherTestResult = HouseworkTestResult.builder()
                .user(other)
                .resultType(TestResultType.PERFECTIONIST)
                .build();

        // Stubbing
        given(userRepository.findById(myUserId)).willReturn(Optional.of(me));

        // 내 정보 조회 (AGREED 상태)
        given(groupMemberRepository.findByUser_UserIdAndStatus(myUserId, MemberStatus.AGREED))
                .willReturn(Optional.of(myMemberInfo));

        // 그룹 멤버 목록 조회 (나 + 다른 멤버)
        given(groupMemberRepository.findByGroup_GroupIdAndStatus(groupId, MemberStatus.AGREED))
                .willReturn(List.of(myMemberInfo, otherMemberInfo));

        // 성향 테스트 결과 조회 (다른 멤버의 결과만 존재한다고 가정)
        given(houseworkTestResultRepository.findByUser_UserIdIn(anyList()))
                .willReturn(List.of(otherTestResult));

        // when
        FeedbackResponse response = feedbackService.getFeedback(myUserId);

        // then
        // 1. 멤버 리스트 검증 (나는 제외되어야 함)
        assertThat(response.groupMembers()).hasSize(1);
        GroupMemberWithTestResult targetMember = response.groupMembers().get(0);

        assertThat(targetMember.groupMemberId()).isEqualTo(otherMemberInfo.getGroupMemberId());
        assertThat(targetMember.nickname()).isEqualTo("멤버");
        assertThat(targetMember.testResultType()).isEqualTo(TestResultType.PERFECTIONIST);

        // 2. 칭찬 스티커 & 카테고리 검증
        assertThat(response.praiseTypes()).isNotEmpty();
        assertThat(response.taskCategories()).isNotEmpty();

        // 3. 메서드 호출 검증
        verify(groupMemberRepository).findByUser_UserIdAndStatus(myUserId, MemberStatus.AGREED);
    }

    @Test
    @DisplayName("피드백 폼 조회 성공 - 성향 테스트를 안 한 멤버는 결과가 null이어야 한다")
    void getFeedbackSuccessWithNullTestResult() {
        // given
        Long myUserId = 1L;
        Long otherUserId = 2L;
        Long groupId = 10L;

        User me = createUser(myUserId, "나");
        User other = createUser(otherUserId, "테스트안한멤버");
        Group group = Group.builder().groupId(groupId).build();

        GroupMember myMemberInfo = createGroupMember(100L, me, group);
        GroupMember otherMemberInfo = createGroupMember(200L, other, group);

        given(userRepository.findById(myUserId)).willReturn(Optional.of(me));
        given(groupMemberRepository.findByUser_UserIdAndStatus(myUserId, MemberStatus.AGREED))
                .willReturn(Optional.of(myMemberInfo));
        given(groupMemberRepository.findByGroup_GroupIdAndStatus(groupId, MemberStatus.AGREED))
                .willReturn(List.of(myMemberInfo, otherMemberInfo));

        // 테스트 결과가 아무것도 없음
        given(houseworkTestResultRepository.findByUser_UserIdIn(anyList()))
                .willReturn(List.of());

        // when
        FeedbackResponse response = feedbackService.getFeedback(myUserId);

        // then
        assertThat(response.groupMembers()).hasSize(1);
        assertThat(response.groupMembers().get(0).testResultType()).isNull(); // null 확인
    }

    @Test
    @DisplayName("피드백 폼 조회 실패 - 요청한 userId가 null이면 예외가 발생한다")
    void getFeedbackFailWhenUserIdIsNull() {
        // given
        Long userId = null;

        // when & then
        assertThatThrownBy(() -> feedbackService.getFeedback(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("피드백 폼 조회 실패 - 존재하지 않는 유저라면 예외가 발생한다")
    void getFeedbackFailWhenUserNotFound() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedbackService.getFeedback(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("피드백 폼 조회 실패 - 협약서에 동의하지 않은 멤버라면 예외가 발생한다")
    void getFeedbackFailWhenNotAgreed() {
        // given
        Long userId = 1L;
        User user = createUser(userId, "미동의자");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // AGREED 상태로 조회했을 때 없다고 반환
        given(groupMemberRepository.findByUser_UserIdAndStatus(userId, MemberStatus.AGREED))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedbackService.getFeedback(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NEED_AGREEMENT_APPROVAL);
    }

    @Test
    @DisplayName("피드백 제출 성공 - 칭찬 스티커와 개선 피드백을 모두 포함하여 저장한다")
    void postFeedbackSuccess() {
        // given
        Long authorUserId = 1L;
        Long targetMemberId = 200L;
        Long groupId = 10L;

        User me = createUser(authorUserId, "나");
        User other = createUser(2L, "너");
        Group group = Group.builder().groupId(groupId).build();

        GroupMember author = createGroupMember(100L, me, group);
        GroupMember target = createGroupMember(targetMemberId, other, group);

        // 요청 DTO 생성 (개선사항 포함)
        FeedbackCreateRequest.ImprovementRequest improvementReq =
                new FeedbackCreateRequest.ImprovementRequest(TaskCategory.BATHROOM, "청소 좀 해");
        FeedbackCreateRequest request = createFeedbackRequest(targetMemberId, List.of(improvementReq));

        given(groupMemberRepository.findByUser_UserIdAndStatus(authorUserId, MemberStatus.AGREED))
                .willReturn(Optional.of(author));
        given(groupMemberRepository.findById(targetMemberId))
                .willReturn(Optional.of(target));

        // when
        feedbackService.postFeedback(authorUserId, request);

        // then
        // 1. save가 호출되었는지 검증
        ArgumentCaptor<Feedback> feedbackCaptor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackRepository).save(feedbackCaptor.capture());

        // 2. 저장된 데이터 검증
        Feedback savedFeedback = feedbackCaptor.getValue();
        assertThat(savedFeedback.getAuthor().getGroupMemberId()).isEqualTo(100L);
        assertThat(savedFeedback.getTarget().getGroupMemberId()).isEqualTo(200L);
        assertThat(savedFeedback.getPraiseTypes()).contains(PraiseType.DETAIL_KING);
        assertThat(savedFeedback.getImprovements()).hasSize(1);
        assertThat(savedFeedback.getImprovements().get(0).getRawText()).isEqualTo("청소 좀 해");
    }

    @Test
    @DisplayName("피드백 제출 성공 - 개선 피드백 없이 칭찬 스티커만 보내도 저장된다")
    void postFeedbackSuccessWithoutImprovements() {
        // given
        Long authorUserId = 1L;
        Long targetMemberId = 200L;
        Group group = Group.builder().groupId(10L).build();

        GroupMember author = createGroupMember(100L, createUser(authorUserId, "나"), group);
        GroupMember target = createGroupMember(targetMemberId, createUser(2L, "너"), group);

        // 요청 DTO 생성 (개선사항 null)
        FeedbackCreateRequest request = createFeedbackRequest(targetMemberId, null);

        given(groupMemberRepository.findByUser_UserIdAndStatus(authorUserId, MemberStatus.AGREED))
                .willReturn(Optional.of(author));
        given(groupMemberRepository.findById(targetMemberId))
                .willReturn(Optional.of(target));

        // when
        feedbackService.postFeedback(authorUserId, request);

        // then
        ArgumentCaptor<Feedback> feedbackCaptor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackRepository).save(feedbackCaptor.capture());

        Feedback savedFeedback = feedbackCaptor.getValue();
        assertThat(savedFeedback.getImprovements()).isEmpty(); // 개선사항 리스트가 비어있는지 확인
    }

    @Test
    @DisplayName("피드백 제출 실패 - 본인에게 피드백을 보낼 수 없다")
    void postFeedbackFailSelfFeedback() {
        // given
        Long authorUserId = 1L;
        Long myMemberId = 100L;
        Group group = Group.builder().groupId(10L).build();

        // 작성자와 대상자가 동일한 Member ID를 가짐
        GroupMember author = createGroupMember(myMemberId, createUser(authorUserId, "나"), group);
        GroupMember target = createGroupMember(myMemberId, createUser(authorUserId, "나"), group);

        FeedbackCreateRequest request = createFeedbackRequest(myMemberId, null);

        given(groupMemberRepository.findByUser_UserIdAndStatus(authorUserId, MemberStatus.AGREED))
                .willReturn(Optional.of(author));
        given(groupMemberRepository.findById(myMemberId))
                .willReturn(Optional.of(target));

        // when & then
        assertThatThrownBy(() -> feedbackService.postFeedback(authorUserId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_FEEDBACK_SELF);
    }

    @Test
    @DisplayName("피드백 제출 실패 - 다른 그룹의 멤버에게 피드백을 보낼 수 없다")
    void postFeedbackFailDifferentGroup() {
        // given
        Long authorUserId = 1L;
        Long targetMemberId = 200L;

        // 서로 다른 그룹 ID
        Group groupA = Group.builder().groupId(10L).build();
        Group groupB = Group.builder().groupId(20L).build();

        GroupMember author = createGroupMember(100L, createUser(authorUserId, "나"), groupA);
        GroupMember target = createGroupMember(targetMemberId, createUser(2L, "남"), groupB);

        FeedbackCreateRequest request = createFeedbackRequest(targetMemberId, null);

        given(groupMemberRepository.findByUser_UserIdAndStatus(authorUserId, MemberStatus.AGREED))
                .willReturn(Optional.of(author));
        given(groupMemberRepository.findById(targetMemberId))
                .willReturn(Optional.of(target));

        // when & then
        assertThatThrownBy(() -> feedbackService.postFeedback(authorUserId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_SAME_GROUP);
    }

    @Test
    @DisplayName("피드백 제출 실패 - 받는 대상(Target)이 존재하지 않으면 예외가 발생한다")
    void postFeedbackFailTargetNotFound() {
        // given
        Long authorUserId = 1L;
        Long unknownTargetId = 999L;
        Group group = Group.builder().groupId(10L).build();

        GroupMember author = createGroupMember(100L, createUser(authorUserId, "나"), group);
        FeedbackCreateRequest request = createFeedbackRequest(unknownTargetId, null);

        given(groupMemberRepository.findByUser_UserIdAndStatus(authorUserId, MemberStatus.AGREED))
                .willReturn(Optional.of(author));

        // 대상 조회 시 Empty 반환
        given(groupMemberRepository.findById(unknownTargetId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedbackService.postFeedback(authorUserId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    // --- 헬퍼 메서드 ---
    private User createUser(Long id, String nickname) {
        return User.builder()
                .userId(id)
                .nickname(nickname)
                .profileImg("http://img.url")
                .build();
    }

    private GroupMember createGroupMember(Long id, User user, Group group) {
        return GroupMember.builder()
                .groupMemberId(id)
                .user(user)
                .group(group)
                .status(MemberStatus.AGREED)
                .build();
    }

    private FeedbackCreateRequest createFeedbackRequest(Long targetId, List<FeedbackCreateRequest.ImprovementRequest> improvements) {
        return FeedbackCreateRequest.builder()
                .targetMemberId(targetId)
                .praiseTypes(List.of(PraiseType.DETAIL_KING))
                .improvements(improvements)
                .build();
    }
}