package com.catchsolmind.cheongyeonbe.domain.oauth.dto.response;

/*
 * 카카오에서 제공받은 토큰으로 조회한 사용자 정보
 * 외부 API 응답 DTO
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record KakaoUserResponse(
        Long id, // providerId

        @JsonProperty("kakao-account")
        KakaoAccount kakaoAccount
) {
    @Builder
    public record KakaoAccount(
            Profile profile
    ) {
        @Builder
        public record Profile(
                String email, // User.email
                String nickname // User.nickname
        ) {
        }
    }
}
