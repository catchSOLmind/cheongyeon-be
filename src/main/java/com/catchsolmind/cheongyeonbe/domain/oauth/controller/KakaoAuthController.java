//package com.catchsolmind.cheongyeonbe.domain.oauth.controller;
//
//import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
//import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///*
// * FE가 준 인가코드를 받아서 Service로 위임
// */
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/oauth/kakao")
//@Slf4j
//public class KakaoAuthController {
//    private final KakaoAuthService kakaoAuthService;
//
//    @PostMapping("/login")
//    public ResponseEntity<KakaoLoginResponse> login(
//            @RequestParam("code") String code
//    ) {
//        KakaoLoginResponse response = kakaoAuthService.login(code);
//
//        return ResponseEntity.ok(response);
//    }
//}
