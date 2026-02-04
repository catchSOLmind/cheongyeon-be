package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupDashboardResponse;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.service.GroupDashboardService;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupDashboardController {

    private final GroupDashboardService dashboardService;
    private final GroupMemberRepository groupMemberRepository;

    @GetMapping("/{groupId}/dashboard")
    @Operation(summary = "우리집 관리 대시보드 조회")
    public GroupDashboardResponse getDashboard(
            @PathVariable Long groupId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();

        groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + user.getUserId() + " is not a member of group " + groupId
                ));

        return dashboardService.getDashboard(groupId);
    }
}
