package com.catchsolmind.cheongyeonbe.domain.task.controller;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.*;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.*;
import com.catchsolmind.cheongyeonbe.domain.task.service.MyTaskCommandService;
import com.catchsolmind.cheongyeonbe.domain.task.service.MyTaskQueryService;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-tasks")
@Slf4j
public class MyTaskController {

    private final MyTaskQueryService queryService;
    private final MyTaskCommandService commandService;
    private final GroupMemberRepository groupMemberRepository;

    private void validatePrincipal(JwtUserDetails principal) {
        if (principal == null) {
            log.error("[Auth] @AuthenticationPrincipal 주입 실패: principal is null");
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    private GroupMember getGroupMember(Long groupId, JwtUserDetails principal) {
        validatePrincipal(principal);
        return groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, principal.user().getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + principal.user().getUserId() + " is not a member of group " + groupId
                ));
    }

    @GetMapping
    @Operation(summary = "내 할 일 목록 조회")
    public MyTaskListResponse getMyTasks(
            @RequestParam Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        GroupMember member = getGroupMember(groupId, principal);
        return queryService.getMyTasks(groupId, member.getGroupMemberId(), date);
    }

    @GetMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 상세 조회")
    public MyTaskDetailResponse getMyTaskDetail(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        getGroupMember(groupId, principal); // 권한 체크
        return queryService.getMyTaskDetail(groupId, occurrenceId);
    }

    @PostMapping
    @Operation(summary = "내 할 일 추가")
    public MyTaskCreateResponse createMyTasks(
            @RequestBody MyTaskCreateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        GroupMember member = getGroupMember(request.getGroupId(), principal);
        return commandService.createMyTasks(member.getGroupMemberId(), request);
    }

    @PatchMapping("/{occurrenceId}/status")
    @Operation(summary = "내 할 일 상태 변경")
    public MyTaskStatusUpdateResponse updateStatus(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskStatusUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        GroupMember member = getGroupMember(groupId, principal);
        return commandService.updateStatus(
                member.getGroupMemberId(),
                groupId,
                occurrenceId,
                request
        );
    }

    @PatchMapping("/{occurrenceId}/schedule")
    @Operation(summary = "내 할 일 일정 변경")
    public MyTaskScheduleUpdateResponse updateSchedule(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskScheduleUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        GroupMember member = getGroupMember(groupId, principal);
        return commandService.updateSchedule(member.getGroupMemberId(), groupId, occurrenceId, request);
    }

    @PostMapping("/{occurrenceId}/request")
    @Operation(summary = "내 할 일 멤버에게 부탁하기")
    public MyTaskRequestToMemberResponse requestToMember(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskRequestToMemberRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        GroupMember member = getGroupMember(groupId, principal);
        return commandService.requestToMember(
                member.getGroupMemberId(),
                groupId,
                occurrenceId,
                request
        );
    }

    @PatchMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 수정")
    public MyTaskUpdateResponse updateMyTask(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        getGroupMember(groupId, principal); // 권한 체크
        return commandService.updateMyTask(groupId, occurrenceId, request);
    }

    @DeleteMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 삭제")
    public MyTaskDeleteResponse deleteMyTask(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        getGroupMember(groupId, principal); // 권한 체크
        return commandService.deleteMyTask(groupId, occurrenceId);
    }
}
