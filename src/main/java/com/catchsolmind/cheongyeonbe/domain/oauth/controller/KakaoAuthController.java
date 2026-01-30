package com.catchsolmind.cheongyeonbe.domain.oauth.controller;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/*
 * FE가 준 인가코드를 받아서 Service로 위임
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth/kakao")
@Validated
@Slf4j
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/login")
    public ResponseEntity<KakaoLoginResponse> login(
            @NotBlank @RequestParam("code") String code
    ) {
        KakaoLoginResponse response = kakaoAuthService.login(code);

        return ResponseEntity.ok(response);
    }



    @Operation(summary = "임시발급용")
    @GetMapping("/callback")
    public String callback(@RequestParam String code) {
        return "code=" + code;
    }


}
