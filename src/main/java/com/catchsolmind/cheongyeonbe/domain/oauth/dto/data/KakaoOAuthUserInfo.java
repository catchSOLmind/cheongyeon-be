package com.catchsolmind.cheongyeonbe.domain.oauth.dto.data;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import lombok.RequiredArgsConstructor;

/*
 * 카카오 전용 User Info
 */

@RequiredArgsConstructor
public class KakaoOAuthUserInfo {
    private final KakaoUserResponse response;

    public OAuthUserInfo toOAuthUserInfo() {
        return new OAuthUserInfo(
                AuthProvider.KAKAO,
                response.id(),
                response.kakaoAccount().profile().email(),
                response.kakaoAccount().profile().nickname()
        );
    }
}
