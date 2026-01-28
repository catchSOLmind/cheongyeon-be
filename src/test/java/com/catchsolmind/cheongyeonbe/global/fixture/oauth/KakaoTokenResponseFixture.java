package com.catchsolmind.cheongyeonbe.global.fixture.oauth;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;

public class KakaoTokenResponseFixture {

    public static KakaoTokenResponse valid() {
        return KakaoTokenResponse.builder()
                .token_type("bearer")
                .access_token("access-token")
                .expires_in(21599)
                .refresh_token("refresh-token")
                .refresh_token_expires_in(5183999)
                .build();
    }
}
