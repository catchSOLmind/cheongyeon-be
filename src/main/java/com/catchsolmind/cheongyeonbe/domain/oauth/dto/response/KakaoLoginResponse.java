package com.catchsolmind.cheongyeonbe.domain.oauth.dto.response;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import lombok.Builder;

@Builder
public record KakaoLoginResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String nickname,
        String profileImg
) {
    public static KakaoLoginResponse of(
            User user,
            String accessToken,
            String refreshToken
    ) {
        return new KakaoLoginResponse(
                accessToken,
                refreshToken,
                user.getUserId(),
                user.getNickname(),
                user.getProfileImg()
        );
    }
}
