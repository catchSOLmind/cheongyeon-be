package com.catchsolmind.cheongyeonbe.domain.auth.dto.response;

import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import lombok.Builder;

@Builder
public record KakaoLoginResponse(
        String accessToken,
        Integer expiresIn,
        String refreshToken,
        Integer refreshTokenExpiresIn,
        UserDto user
) {
    public static KakaoLoginResponse of(
            UserDto userDto,
            String accessToken,
            long accessTokenExpirationMs,
            String refreshToken,
            long refreshTokenExpirationMs
    ) {
        return new KakaoLoginResponse(
                accessToken,
                (int) (accessTokenExpirationMs / 1000),
                refreshToken,
                (int) (refreshTokenExpirationMs / 1000),
                userDto
        );
    }
}
