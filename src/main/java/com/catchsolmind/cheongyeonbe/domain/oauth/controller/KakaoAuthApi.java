package com.catchsolmind.cheongyeonbe.domain.oauth.controller;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.request.RefreshTokenRequest;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.RefreshTokenResponse;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "OAuth", description = "카카오 로그인 및 토큰 관리 API")
public interface KakaoAuthApi {
    @Operation(
            summary = "인가 코드 콜백",
            description = "카카오로부터 인가 코드를 전달받는 엔드포인트입니다. (로컬 테스트용)"
    )
    String getKakaoCode(
            @Parameter(description = "카카오 인가 코드", required = true)
            @RequestParam("code") String code
    );

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 인가 코드를 통해 로그인을 진행하고 JWT 토큰을 발급합니다."
    )
    ApiResponse<KakaoLoginResponse> login(
            @Parameter(description = "카카오 인가 코드", required = true)
            @RequestParam("code") String code,
            @Parameter(description = "카카오 Redirect URI (필요 시)")
            @RequestParam(value = "redirectUri", required = false) String redirectUri
    );

    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다."
    )
    ApiResponse<RefreshTokenResponse> refresh(
            @RequestBody RefreshTokenRequest request
    );
}
