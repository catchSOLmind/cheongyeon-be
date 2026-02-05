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
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
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


    private GroupMember currentMember(User user) {
        return groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("그룹 미가입 사용자"));
    }


    @GetMapping
    @Operation(summary = "내 할 일 목록 조회")
    public MyTaskListResponse getMyTasks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        return queryService.getMyTasks(member, date);
    }

    @GetMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 상세 조회")
    public MyTaskDetailResponse getMyTaskDetail(
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return queryService.getMyTaskDetail(member, occurrenceId);
    }


    @PostMapping
    @Operation(summary = "내 할 일 추가")
    public MyTaskCreateResponse createMyTasks(
            @RequestBody MyTaskCreateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return commandService.createMyTasks(member, request);
    }



    @PatchMapping("/{occurrenceId}/status")
    @Operation(summary = "내 할 일 상태 변경")
    public MyTaskStatusUpdateResponse updateStatus(
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskStatusUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return commandService.updateStatus(member, occurrenceId, request);
    }

    @PostMapping("/{occurrenceId}/complete")
    @Operation(summary = "내 할 일 완료하기", description = "할일을 완료 상태로 변경합니다. Request body 없이 호출만 하면 됩니다.")
    public MyTaskCompleteResponse completeTask(
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return commandService.completeTask(member, occurrenceId);
    }

    @PatchMapping("/{occurrenceId}/schedule")
    @Operation(summary = "내 할 일 일정 변경")
    public MyTaskScheduleUpdateResponse updateSchedule(
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskScheduleUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return commandService.updateSchedule(member, occurrenceId, request);
    }

    @PostMapping("/{occurrenceId}/request")
    @Operation(summary = "내 할 일 멤버에게 부탁하기")
    public MyTaskRequestToMemberResponse requestToMember(
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskRequestToMemberRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return commandService.requestToMember(member, occurrenceId, request);
    }

    @PatchMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 수정")
    public MyTaskUpdateResponse updateMyTask(
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return commandService.updateMyTask(member, occurrenceId, request);
    }

    @DeleteMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 삭제")
    public MyTaskDeleteResponse deleteMyTask(
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);

        return commandService.deleteMyTask(member, occurrenceId);
    }
}
