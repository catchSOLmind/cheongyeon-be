package com.catchsolmind.cheongyeonbe.domain.oauth.controller;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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

}
