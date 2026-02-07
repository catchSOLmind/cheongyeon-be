package com.catchsolmind.cheongyeonbe.domain.feedback.service;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.FeedbackCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.GroupMemberWithTestResult;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.PraiseTypeResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.entity.Feedback;
import com.catchsolmind.cheongyeonbe.domain.feedback.entity.ImprovementFeedback;
import com.catchsolmind.cheongyeonbe.domain.feedback.repository.FeedbackRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestResultRepository;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FeedbackService {
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final HouseworkTestResultRepository houseworkTestResultRepository;
    private final FeedbackRepository feedbackRepository;

    public FeedbackResponse getFeedback(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupMember currentMember = groupMemberRepository.findByUser_UserIdAndStatus(userId, MemberStatus.AGREED)
                .orElseThrow(() -> new BusinessException(ErrorCode.NEED_AGREEMENT_APPROVAL));

        Long groupId = currentMember.getGroup().getGroupId();
        List<GroupMember> groupMembers = groupMemberRepository.findByGroup_GroupIdAndStatus(groupId, MemberStatus.AGREED);

        List<Long> userIds = groupMembers.stream()
                .map(gm -> gm.getUser().getUserId())
                .toList();

        Map<Long, TestResultType> testResultMap = houseworkTestResultRepository.findByUser_UserIdIn(userIds).stream()
                .collect(Collectors.toMap(
                        result -> result.getUser().getUserId(),
                        HouseworkTestResult::getResultType,
                        (existing, replacement) -> existing
                ));

        List<GroupMemberWithTestResult> memberDtos = groupMembers.stream()
                .filter(gm -> !gm.getUser().getUserId().equals(userId)) // 본인 제외
                .map(gm -> {
                    Long memberUserId = gm.getUser().getUserId();
                    return GroupMemberWithTestResult.builder()
                            .groupMemberId(gm.getGroupMemberId())
                            .nickname(gm.getUser().getNickname())
                            .profileImageUrl(gm.getUser().getProfileImg())
                            .testResultType(testResultMap.get(memberUserId))
                            .build();
                })
                .toList();

        List<PraiseTypeResponse> praiseTypes = Arrays.stream(PraiseType.values())
                .map(PraiseTypeResponse::from)
                .toList();
        List<TaskCategory> taskCategories = Arrays.asList(TaskCategory.values());

        return new FeedbackResponse(memberDtos, praiseTypes, taskCategories);
    }

    @Transactional
    public void postFeedback(Long userId, FeedbackCreateRequest request) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // Author 조회 및 검증
        GroupMember author = groupMemberRepository.findByUser_UserIdAndStatus(userId, MemberStatus.AGREED)
                .orElseThrow(() -> new BusinessException(ErrorCode.NEED_AGREEMENT_APPROVAL));

        // Target 조회
        GroupMember target = groupMemberRepository.findById(request.targetMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 유효성 검증
        // 본인에게 쓰는지 확인
        if (author.getGroupMemberId().equals(target.getGroupMemberId())) {
            throw new BusinessException(ErrorCode.CANNOT_FEEDBACK_SELF);
        }
        // 같은 그룹인지 확인
        if (!author.getGroup().getGroupId().equals(target.getGroup().getGroupId())) {
            throw new BusinessException(ErrorCode.NOT_SAME_GROUP);
        }

        // ImprovementFeedback 엔티티 변환
        List<ImprovementFeedback> improvementEntities = new ArrayList<>();
        if (request.improvements() != null && !request.improvements().isEmpty()) {
            improvementEntities = request.improvements().stream()
                    .map(dto -> ImprovementFeedback.builder()
                            .category(dto.category())
                            .rawText(dto.content())
                            .aiStatus(AiStatus.UNCOMPLETED) // 초기값 설정
                            .build())
                    .toList();
        }

        // Feedback 엔티티 생성 및 매핑
        Feedback feedback = Feedback.builder()
                .group(author.getGroup())
                .author(author)
                .target(target)
                .praiseTypes(request.praiseTypes())
                .improvements(new ArrayList<>(improvementEntities))
                .build();

        // 저장
        feedbackRepository.save(feedback);

        log.info("[피드백] 작성자: {}, 대상자: {}, 칭찬: {}, 개선사항수: {}",
                author.getGroupMemberId(), target.getGroupMemberId(), request.praiseTypes().size(), improvementEntities.size());
    }
}
