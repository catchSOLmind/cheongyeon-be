package com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.user.UserDtoFixture;

public class KakaoLoginResponseFixture {

    public static KakaoLoginResponse valid() {
        return KakaoLoginResponse.builder()
                .accessToken("access-token")
                .expiresIn(3600)
                .refreshToken("refresh-token")
                .refreshTokenExpiresIn(1209600000)
                .user(UserDtoFixture.valid()).build();
    }
}
