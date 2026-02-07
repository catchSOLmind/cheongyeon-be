package com.catchsolmind.cheongyeonbe.domain.feedback.service;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.GroupMemberWithTestResult;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.PraiseTypeResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestResultRepository;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.enums.PraiseType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final HouseworkTestResultRepository houseworkTestResultRepository;

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
}
