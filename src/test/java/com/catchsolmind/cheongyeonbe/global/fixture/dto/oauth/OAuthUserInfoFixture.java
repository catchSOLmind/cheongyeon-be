package com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;

public class OAuthUserInfoFixture {

    public static OAuthUserInfo kakaoUser() {
        return OAuthUserInfo.builder()
                .provider(AuthProvider.KAKAO)
                .providerId(123456789L)
                .email("email@email.com")
                .nickname("nickname")
                .build();
    }
}
