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
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
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

    private GroupMember currentMember(User user) {
        return groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("그룹 미가입 사용자"));
    }

    @GetMapping
    @Operation(summary = "세부 업무 조회", description = "카테고리별 세부 업무를 조회합니다. 새해맞이/성향별 추천 업무 섹션 포함")
    public ApiResponse<TaskTypeListResponse> getTaskTypes(
            @RequestParam(required = false) TaskCategory category,
            @RequestParam(required = false) String subCategory,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        TaskSubCategory parsed = null;
        if (subCategory != null && !subCategory.isBlank()) {
            parsed = TaskSubCategory.valueOf(subCategory);
        }

        User user = (principal != null) ? principal.user() : null;
        TaskTypeListResponse response = taskTypeService.getTaskTypesByCategory(category, parsed, user);
        return ApiResponse.success("세부 업무 조회 성공", response);
    }

    @GetMapping("/favorites")
    @Operation(summary = "즐겨찾기 세부 업무 조회")
    public ApiResponse<TaskTypeListResponse> getFavorites(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        TaskTypeListResponse response = taskTypeService.getFavoriteTaskTypes(member.getGroupMemberId());
        return ApiResponse.success("즐겨찾기 세부 업무 조회 성공", response);
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
            @PathVariable Long taskTypeId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        TaskTypeFavoriteResponse response = taskTypeService.addFavorite(member.getGroupMemberId(), taskTypeId);
        return ApiResponse.success("즐겨찾기 추가 성공", response);
    }

    @DeleteMapping("/{taskTypeId}/favorite")
    @Operation(summary = "즐겨찾기 해제")
    public ApiResponse<TaskTypeFavoriteResponse> removeFavorite(
            @PathVariable Long taskTypeId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        TaskTypeFavoriteResponse response = taskTypeService.removeFavorite(member.getGroupMemberId(), taskTypeId);
        return ApiResponse.success("즐겨찾기 해제 성공", response);
    }
}
