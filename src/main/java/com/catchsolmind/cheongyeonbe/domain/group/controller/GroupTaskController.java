package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskDetailResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskListResponse;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.service.GroupTaskQueryService;
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
@RequestMapping("/api/group-tasks")
@Slf4j
public class GroupTaskController {

    private final GroupTaskQueryService groupTaskQueryService;
    private final GroupMemberRepository groupMemberRepository;

    private void validatePrincipal(JwtUserDetails principal) {
        if (principal == null) {
            log.error("[Auth] @AuthenticationPrincipal 주입 실패: principal is null");
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    private void validateGroupMember(Long groupId, JwtUserDetails principal) {
        validatePrincipal(principal);
        groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, principal.user().getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + principal.user().getUserId() + " is not a member of group " + groupId
                ));
    }

    @GetMapping
    @Operation(summary = "전체 할일 목록 조회")
    public GroupTaskListResponse getGroupTasks(
            @RequestParam Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        validateGroupMember(groupId, principal);
        return groupTaskQueryService.getGroupTasks(groupId, date);
    }

    @GetMapping("/{occurrenceId}")
    @Operation(summary = "전체 할일 상세 조회")
    public GroupTaskDetailResponse getGroupTaskDetail(
            @RequestParam Long groupId,
            @PathVariable Long occurrenceId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        validateGroupMember(groupId, principal);
        return groupTaskQueryService.getGroupTaskDetail(groupId, occurrenceId);
    }
}
