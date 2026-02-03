package com.catchsolmind.cheongyeonbe.domain.group.controller;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupDashboardResponse;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.service.GroupDashboardService;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@Slf4j
public class GroupDashboardController {

    private final GroupDashboardService dashboardService;
    private final GroupMemberRepository groupMemberRepository;

    private void validatePrincipal(JwtUserDetails principal) {
        if (principal == null) {
            log.error("[Auth] @AuthenticationPrincipal 주입 실패: principal is null");
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    @GetMapping("/{groupId}/dashboard")
    @Operation(summary = "우리집 관리 대시보드 조회")
    public GroupDashboardResponse getDashboard(
            @PathVariable Long groupId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        validatePrincipal(principal);

        // 권한 체크: 요청자가 해당 그룹의 멤버인지 확인
        groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, principal.user().getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + principal.user().getUserId() + " is not a member of group " + groupId
                ));

        return dashboardService.getDashboard(groupId);
    }
}
