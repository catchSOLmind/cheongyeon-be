package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupInvitationAcceptResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupInvitationCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.service.GroupService;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@Tag(name = "Group", description = "그룹 생성/초대 API")
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberRepository groupMemberRepository;

    private GroupMember currentMember(User user) {
        return groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("그룹 미가입 사용자"));
    }

    @PostMapping
    @Operation(summary = "그룹 생성", description = "새 그룹을 생성하고 OWNER로 등록됩니다. 기존 임시 그룹에서는 탈퇴됩니다.")
    public ApiResponse<GroupCreateResponse> createGroup(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupCreateResponse response = groupService.createGroup(user);
        return ApiResponse.success("그룹 생성 성공", response);
    }

    @PostMapping("/invitations")
    @Operation(summary = "초대 링크 생성", description = "그룹 OWNER만 초대 링크를 생성할 수 있습니다.")
    public ApiResponse<GroupInvitationCreateResponse> createInvitation(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        GroupInvitationCreateResponse response = groupService.createInvitation(member);
        return ApiResponse.success("초대 링크 생성 성공", response);
    }

    @PostMapping("/invitations/{invitationId}/accept")
    @Operation(summary = "초대 수락 (그룹 가입)", description = "초대 링크를 통해 그룹에 가입합니다. 기존 임시 그룹에서는 탈퇴됩니다.")
    public ApiResponse<GroupInvitationAcceptResponse> acceptInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupInvitationAcceptResponse response = groupService.acceptInvitation(user, invitationId);
        return ApiResponse.success("그룹 가입 성공", response);
    }
}
