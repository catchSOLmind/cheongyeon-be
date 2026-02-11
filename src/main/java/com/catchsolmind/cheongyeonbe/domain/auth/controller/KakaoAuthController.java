package com.catchsolmind.cheongyeonbe.domain.auth.controller;

import com.catchsolmind.cheongyeonbe.domain.auth.dto.request.RefreshTokenRequest;
import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.RefreshTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.auth.service.AuthTokenService;
import com.catchsolmind.cheongyeonbe.domain.auth.service.KakaoAuthService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@Validated
@Slf4j
public class KakaoAuthController implements KakaoAuthApi {
    private final KakaoAuthService kakaoAuthService;
    private final AuthTokenService authTokenService;

    @GetMapping("/kakao/callback")
    public String getKakaoCode(@RequestParam("code") String code) {
        return code;
    }

    @PostMapping("/kakao/login")
    public ApiResponse<KakaoLoginResponse> login(
            @RequestParam("code") String code,
            @RequestParam(value = "redirectUri", required = false) String redirectUri
    ) {
        KakaoLoginResponse response = kakaoAuthService.login(code, redirectUri);

        return ApiResponse.success(response);
    }

    @PostMapping("/kakao/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ) {
        return ApiResponse.success(
                authTokenService.refresh(request)
        );
    }

}
