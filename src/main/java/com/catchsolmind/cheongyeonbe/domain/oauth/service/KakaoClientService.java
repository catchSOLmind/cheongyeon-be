package com.catchsolmind.cheongyeonbe.domain.oauth.service;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;

public interface KakaoClientService {

    KakaoTokenResponse requestToken(String code, String redirectUri);

    String getDefaultRedirectUri();

    KakaoUserResponse getKakaoUserInfo(String accessToken);
}
