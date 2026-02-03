package com.catchsolmind.cheongyeonbe.domain.task.controller;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.TaskTypeCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeFavoriteResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeListResponse;
import com.catchsolmind.cheongyeonbe.domain.task.service.TaskTypeService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task-types")
@Slf4j
public class TaskTypeController {

    private final TaskTypeService taskTypeService;
    private final GroupMemberRepository groupMemberRepository;

    private void validatePrincipal(@AuthenticationPrincipal JwtUserDetails principal) {
        if (principal == null) {
            log.error("[Auth] @AuthenticationPrincipal 주입 실패: principal is null");
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    private GroupMember getGroupMember(Long groupId, @AuthenticationPrincipal JwtUserDetails principal) {
        validatePrincipal(principal);
        return groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, principal.user().getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + principal.user().getUserId() + " is not a member of group " + groupId
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
        GroupMember member = getGroupMember(groupId, principal);
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
        validatePrincipal(principal);
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
        GroupMember member = getGroupMember(groupId, principal);
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
        GroupMember member = getGroupMember(groupId, principal);
        TaskTypeFavoriteResponse response = taskTypeService.removeFavorite(member.getGroupMemberId(), taskTypeId);
        return ApiResponse.success("즐겨찾기 해제 성공", response);
    }
}
