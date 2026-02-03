package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupMemberListResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@Slf4j
public class GroupMemberController {

    private final GroupMemberRepository groupMemberRepository;

    private void validatePrincipal(JwtUserDetails principal) {
        if (principal == null) {
            log.error("[Auth] @AuthenticationPrincipal 주입 실패: principal is null");
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    @GetMapping("/{groupId}/members")
    @Operation(summary = "그룹 멤버 목록 조회")
    public GroupMemberListResponse getGroupMembers(
            @PathVariable Long groupId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        validatePrincipal(principal);

        // 권한 체크: 요청자가 해당 그룹의 멤버인지 확인
        groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, principal.user().getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + principal.user().getUserId() + " is not a member of group " + groupId
                ));

        // LEFT 상태 제외한 멤버 목록 조회
        List<GroupMember> members = groupMemberRepository.findByGroup_GroupIdAndStatusNot(
                groupId, MemberStatus.LEFT
        );

        List<GroupMemberListResponse.GroupMemberItemDto> memberDtos = members.stream()
                .map(member -> GroupMemberListResponse.GroupMemberItemDto.builder()
                        .memberId(member.getGroupMemberId())
                        .nickname(member.getUser().getNickname())
                        .profileImageUrl(member.getUser().getProfileImg())
                        .role(member.getRole())
                        .status(member.getStatus())
                        .build())
                .collect(Collectors.toList());

        return GroupMemberListResponse.builder()
                .groupId(groupId)
                .members(memberDtos)
                .build();
    }
}
