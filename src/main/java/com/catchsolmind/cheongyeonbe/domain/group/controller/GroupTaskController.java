package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskDetailResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskListResponse;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.service.GroupTaskQueryService;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-tasks")
public class GroupTaskController {

    private final GroupTaskQueryService groupTaskQueryService;
    private final GroupMemberRepository groupMemberRepository;

    private void validateGroupMember(Long groupId, User user) {
        groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + user.getUserId() + " is not a member of group " + groupId
                ));
    }

    @GetMapping
    @Operation(summary = "전체 할일 목록 조회")
    public GroupTaskListResponse getGroupTasks(
            @RequestParam Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        validateGroupMember(groupId, user);
        return groupTaskQueryService.getGroupTasks(groupId, date);
    }

    @GetMapping("/{occurrenceId}")
    @Operation(summary = "전체 할일 상세 조회")
    public GroupTaskDetailResponse getGroupTaskDetail(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        validateGroupMember(groupId, user);
        return groupTaskQueryService.getGroupTaskDetail(groupId, occurrenceId);
    }
}
