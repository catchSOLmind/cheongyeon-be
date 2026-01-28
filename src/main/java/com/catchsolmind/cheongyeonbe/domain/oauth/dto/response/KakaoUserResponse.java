package com.catchsolmind.cheongyeonbe.domain.oauth.dto.response;

/*
 * 카카오에서 제공받은 토큰으로 조회한 사용자 정보
 * UserEntity 생성에 사용
 */

import lombok.Builder;

@Builder
public record KakaoUserResponse(
        Long id, // providerId

        KakaoAccount kakaoAccount
) {
    @Builder
    public record KakaoAccount(
            Profile profile
    ) {
        @Builder
        public record Profile(
                String nickname // User.nickname
        ) {
        }
    }
}
