package com.catchsolmind.cheongyeonbe.domain.auth.service;

import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.KakaoUserResponse;

public interface KakaoClientService {

    KakaoTokenResponse requestToken(String code, String redirectUri);

    String getDefaultRedirectUri();

    KakaoUserResponse getKakaoUserInfo(String accessToken);
}
