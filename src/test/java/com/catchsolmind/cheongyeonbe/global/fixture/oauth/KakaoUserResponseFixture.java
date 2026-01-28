package com.catchsolmind.cheongyeonbe.global.fixture.oauth;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;

public class KakaoUserResponseFixture {

    public static KakaoUserResponse valid() {
        return KakaoUserResponse.builder()
                .id(1L)
                .kakaoAccount(KakaoUserResponse.KakaoAccount.builder()
                        .profile(KakaoUserResponse.KakaoAccount.Profile.builder()
                                .nickname("유저1 닉네임")
                                .build())
                        .build())
                .build();
    }
}
