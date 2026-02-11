package com.catchsolmind.cheongyeonbe.domain.auth.controller;

import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.GuestLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.auth.service.GuestService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게스트 로그인", description = "게스트 로그인 및 데이터 자동 생성")
@RestController
@RequestMapping("/api/auth/guest")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @Operation(summary = "게스트 로그인", description = "유저/그룹/가사/협약서 및 모든 데이터를 모두 자동 생성하고 토큰을 발급합니다.")
    @PostMapping
    public ApiResponse<GuestLoginResponse> guestLogin() {
        GuestLoginResponse response = guestService.enterGuestMode();

        return ApiResponse.success("게스트 로그인 성공", response);
    }
}
