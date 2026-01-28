package com.catchsolmind.cheongyeonbe.global.fixture.oauth;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;

public class KakaoLoginResponseFixture {

    public static KakaoLoginResponse valid() {
        return KakaoLoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .userId(1L)
                .nickname("유저1")
                .profileImg("profile-img-url")
                .build();
    }
}
