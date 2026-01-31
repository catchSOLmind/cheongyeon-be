package com.catchsolmind.cheongyeonbe.domain.oauth.controller;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.request.RefreshTokenRequest;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.RefreshTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.AuthTokenService;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/*
 * FE가 준 인가코드를 받아서 Service로 위임
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@Validated
@Slf4j
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;
    private final AuthTokenService authTokenService;

    @PostMapping("/kakao/login")
    public ApiResponse<KakaoLoginResponse> login(
            @NotBlank @RequestParam("code") String code
    ) {
        log.info("[OAuth] 인가코드 받기 성공: code={}", code);
        KakaoLoginResponse response = kakaoAuthService.login(code);

        return ApiResponse.success(response);
    }

    @PostMapping("/kakao/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ApiResponse.success(
                authTokenService.refresh(request)
        );
    }
}
