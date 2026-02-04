package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupMemberListResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupMemberController {

    private final GroupMemberRepository groupMemberRepository;

    @GetMapping("/{groupId}/members")
    @Operation(summary = "그룹 멤버 목록 조회")
    public GroupMemberListResponse getGroupMembers(
            @PathVariable Long groupId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();

        groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + user.getUserId() + " is not a member of group " + groupId
                ));

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
