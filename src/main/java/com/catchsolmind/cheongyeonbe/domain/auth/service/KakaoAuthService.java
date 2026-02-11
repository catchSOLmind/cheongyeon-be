package com.catchsolmind.cheongyeonbe.domain.auth.service;

import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.KakaoLoginResponse;

public interface KakaoAuthService {
    KakaoLoginResponse login(String code, String redirectUri);}
