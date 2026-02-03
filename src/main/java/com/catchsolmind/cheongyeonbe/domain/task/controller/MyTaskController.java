package com.catchsolmind.cheongyeonbe.domain.task.controller;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.service.GroupMemberService;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.*;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.*;
import com.catchsolmind.cheongyeonbe.domain.task.service.MyTaskCommandService;
import com.catchsolmind.cheongyeonbe.domain.task.service.MyTaskQueryService;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-tasks")
public class MyTaskController {

    private final MyTaskQueryService queryService;
    private final MyTaskCommandService commandService;
    private final GroupMemberRepository groupMemberRepository;


    private GroupMember getGroupMember(Long groupId, User user) {
        return groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + user.getUserId() + " is not a member of group " + groupId
                ));
    }

    @GetMapping
    @Operation(summary = "내 할 일 목록 조회")
    public MyTaskListResponse getMyTasks(
            @RequestParam Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user
    ) {
        GroupMember member = getGroupMember(groupId, user);
        return queryService.getMyTasks(groupId, member.getGroupMemberId(), date);
    }

    @GetMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 상세 조회")
    public MyTaskDetailResponse getMyTaskDetail(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal User user
    ) {
        getGroupMember(groupId, user); // 권한 체크
        return queryService.getMyTaskDetail(groupId, occurrenceId);
    }


    @PostMapping
    @Operation(summary = "내 할 일 추가")
    public MyTaskCreateResponse createMyTasks(
            @RequestBody MyTaskCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        GroupMember member = getGroupMember(request.getGroupId(), user);
        return commandService.createMyTasks(member.getGroupMemberId(), request);
    }



    @PatchMapping("/{occurrenceId}/status")
    @Operation(summary = "내 할 일 상태 변경")
    public MyTaskStatusUpdateResponse updateStatus(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskStatusUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        GroupMember member = getGroupMember(groupId, user);
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
            @AuthenticationPrincipal User user
    ) {
        GroupMember member = getGroupMember(groupId, user);
        return commandService.updateSchedule(member.getGroupMemberId(), groupId, occurrenceId, request);
    }

    @PostMapping("/{occurrenceId}/request")
    @Operation(summary = "내 할 일 멤버에게 부탁하기")
    public MyTaskRequestToMemberResponse requestToMember(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskRequestToMemberRequest request,
            @AuthenticationPrincipal User user
    ) {
        GroupMember member = getGroupMember(groupId, user);
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
            @AuthenticationPrincipal User user
    ) {
        getGroupMember(groupId, user); // 권한 체크
        return commandService.updateMyTask(groupId, occurrenceId, request);
    }

    @DeleteMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 삭제")
    public MyTaskDeleteResponse deleteMyTask(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal User user
    ) {
        getGroupMember(groupId, user); // 권한 체크
        return commandService.deleteMyTask(groupId, occurrenceId);
    }
}
