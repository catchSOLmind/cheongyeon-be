package com.catchsolmind.cheongyeonbe.domain.oauth.service;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;

public interface KakaoAuthService {
    KakaoLoginResponse login(String code);
}
