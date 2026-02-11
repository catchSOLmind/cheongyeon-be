package com.catchsolmind.cheongyeonbe.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record RefreshTokenResponse(
        String accessToken,
        Integer expiresIn,
        String refreshToken,
        Integer refreshTokenExpiresIn
) {
}
