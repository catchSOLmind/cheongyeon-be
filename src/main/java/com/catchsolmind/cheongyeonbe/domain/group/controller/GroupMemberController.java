package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupMemberListResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@Tag(name = "Group Member", description = "그룹 멤버 API")
public class GroupMemberController {

    private final GroupMemberRepository groupMemberRepository;

    private GroupMember currentMember(User user) {
        return groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("그룹 미가입 사용자"));
    }

    @GetMapping("/members")
    @Operation(summary = "그룹 멤버 목록 조회", description = "현재 그룹의 멤버 목록을 조회합니다")
    public ApiResponse<GroupMemberListResponse> getGroupMembers(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        Group group = member.getGroup();

        List<GroupMember> members = groupMemberRepository.findByGroup_GroupIdAndStatusNot(
                group.getGroupId(), MemberStatus.LEFT
        );

        List<GroupMemberListResponse.GroupMemberItemDto> memberDtos = members.stream()
                .map(m -> GroupMemberListResponse.GroupMemberItemDto.builder()
                        .memberId(m.getGroupMemberId())
                        .nickname(m.getUser().getNickname())
                        .profileImageUrl(m.getUser().getProfileImg())
                        .role(m.getRole())
                        .status(m.getStatus())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .collect(Collectors.toList());

        GroupMemberListResponse response = GroupMemberListResponse.builder()
                .groupId(group.getGroupId())
                .memberCount(memberDtos.size())
                .members(memberDtos)
                .build();

        return ApiResponse.success("멤버 목록 조회 성공", response);
    }

    // 기존 API 유지 (하위 호환)
    @GetMapping("/{groupId}/members")
    @Operation(summary = "그룹 멤버 목록 조회 (groupId 지정)", description = "지정한 그룹의 멤버 목록을 조회합니다")
    public ApiResponse<GroupMemberListResponse> getGroupMembersByGroupId(
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
                .map(m -> GroupMemberListResponse.GroupMemberItemDto.builder()
                        .memberId(m.getGroupMemberId())
                        .nickname(m.getUser().getNickname())
                        .profileImageUrl(m.getUser().getProfileImg())
                        .role(m.getRole())
                        .status(m.getStatus())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .collect(Collectors.toList());

        GroupMemberListResponse response = GroupMemberListResponse.builder()
                .groupId(groupId)
                .memberCount(memberDtos.size())
                .members(memberDtos)
                .build();

        return ApiResponse.success("멤버 목록 조회 성공", response);
    }
}
