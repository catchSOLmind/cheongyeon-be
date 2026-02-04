package com.catchsolmind.cheongyeonbe.domain.task.controller;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.TaskTypeCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeFavoriteResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeListResponse;
import com.catchsolmind.cheongyeonbe.domain.task.service.TaskTypeService;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task-types")
public class TaskTypeController {

    private final TaskTypeService taskTypeService;
    private final GroupMemberRepository groupMemberRepository;

    private GroupMember getGroupMember(Long groupId, User user) {
        return groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + user.getUserId() + " is not a member of group " + groupId
                ));
    }

    @GetMapping
    @Operation(summary = "세부 업무 조회", description = "카테고리별 세부 업무를 조회합니다. favorite=true면 즐겨찾기만 조회")
    public ApiResponse<TaskTypeListResponse> getTaskTypes(
            @RequestParam Long groupId,
            @RequestParam(required = false) TaskCategory category,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) String q,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = getGroupMember(groupId, user);
        TaskTypeListResponse response = taskTypeService.getTaskTypes(
                member.getGroupMemberId(),
                category,
                favorite,
                q
        );
        return ApiResponse.success("세부 업무 조회 성공", response);
    }

    @PostMapping
    @Operation(summary = "세부 업무 등록", description = "DB에 없는 세부 업무를 직접 등록합니다")
    public ApiResponse<TaskTypeCreateResponse> createTaskType(
            @RequestBody TaskTypeCreateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        TaskTypeCreateResponse response = taskTypeService.createTaskType(request);
        return ApiResponse.success("세부 업무 직접 등록 성공", response);
    }

    @PostMapping("/{taskTypeId}/favorite")
    @Operation(summary = "즐겨찾기 추가")
    public ApiResponse<TaskTypeFavoriteResponse> addFavorite(
            @RequestParam Long groupId,
            @PathVariable Long taskTypeId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = getGroupMember(groupId, user);
        TaskTypeFavoriteResponse response = taskTypeService.addFavorite(member.getGroupMemberId(), taskTypeId);
        return ApiResponse.success("즐겨찾기 추가 성공", response);
    }

    @DeleteMapping("/{taskTypeId}/favorite")
    @Operation(summary = "즐겨찾기 해제")
    public ApiResponse<TaskTypeFavoriteResponse> removeFavorite(
            @RequestParam Long groupId,
            @PathVariable Long taskTypeId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = getGroupMember(groupId, user);
        TaskTypeFavoriteResponse response = taskTypeService.removeFavorite(member.getGroupMemberId(), taskTypeId);
        return ApiResponse.success("즐겨찾기 해제 성공", response);
    }
}
