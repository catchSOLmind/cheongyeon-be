package com.catchsolmind.cheongyeonbe.global.fixture.oauth;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;

public class KakaoUserResponseFixture {

    public static KakaoUserResponse user1() {
        return KakaoUserResponse.builder()
                .id(1L)
                .kakaoAccount(KakaoUserResponse.KakaoAccount.builder()
                        .profile(KakaoUserResponse.KakaoAccount.Profile.builder()
                                .name("유저1")
                                .email("유저1@email.com")
                                .nickname("유저1 닉네임")
                                .build())
                        .build())
                .build();
    }
}
