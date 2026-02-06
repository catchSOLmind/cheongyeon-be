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

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {
    @Builder
    public record KakaoAccount(
            String email, // User.email
            Profile profile
    ) {
        @Builder
        public record Profile(
                String nickname, // User.nickname

                @JsonProperty("profile_image_url")
                String profileImageUrl, // User.profileImg

                @JsonProperty("is_default_image") // 카카오톡 기본 이미지 여부
                Boolean isDefaultImage
        ) {
        }
    }
}
