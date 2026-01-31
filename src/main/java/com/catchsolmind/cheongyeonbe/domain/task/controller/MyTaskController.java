package com.catchsolmind.cheongyeonbe.domain.task.controller;

import com.catchsolmind.cheongyeonbe.domain.group.service.GroupMemberService;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.MyTaskCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.MyTaskStatusUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskDetailResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskListResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskStatusUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.service.MyTaskCommandService;
import com.catchsolmind.cheongyeonbe.domain.task.service.MyTaskQueryService;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-tasks")
public class MyTaskController {

    private final MyTaskQueryService queryService;
    private final MyTaskCommandService commandService;
    private final GroupMemberService groupMemberService;
    private final UserRepository userRepository;


    // jwt 해결전 임시
    private Long resolveMyMemberId(Long groupId, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required (temporary login mode)");
        }
        return groupMemberService.getMemberId(groupId, userId);
    }

    @GetMapping
    @Operation(summary = "내 할 일 목록 조회")
    public MyTaskListResponse getMyTasks(
            @RequestParam Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long userId
    ) {
        Long myMemberId = resolveMyMemberId(groupId, userId);
        return queryService.getMyTasks(groupId, myMemberId, date);
    }

    @GetMapping("/{occurrenceId}")
    @Operation(summary = "내 할 일 상세 조회")
    public MyTaskDetailResponse getMyTaskDetail(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestParam Long userId
    ) {
        resolveMyMemberId(groupId, userId);
        return queryService.getMyTaskDetail(groupId, occurrenceId);
    }


    @PostMapping
    @Operation(summary = "내 할 일 추가")
    public MyTaskCreateResponse createMyTasks(
            @RequestBody MyTaskCreateRequest request,
            @RequestParam Long userId
    ) {
        Long myMemberId = resolveMyMemberId(request.getGroupId(), userId);
        return commandService.createMyTasks(myMemberId, request);
    }

    @PatchMapping("/{occurrenceId}/status")
    @Operation(summary = "내 할 일 상태 변경")
    public MyTaskStatusUpdateResponse updateStatus(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @RequestBody MyTaskStatusUpdateRequest request,
            @RequestParam Long userId
    ) {
        Long myMemberId = resolveMyMemberId(groupId, userId);
        return commandService.updateStatus(myMemberId, groupId, occurrenceId, request);
    }
}
