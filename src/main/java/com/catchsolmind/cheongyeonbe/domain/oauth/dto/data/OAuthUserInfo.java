package com.catchsolmind.cheongyeonbe.domain.oauth.dto.data;

import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import lombok.Builder;

/*
 * 서비스 공통 모델
 */

@Builder
public record OAuthUserInfo(
        AuthProvider provider,
        Long providerId,
        String email,
        String nickname,
        String profileImageUrl
) {
}
