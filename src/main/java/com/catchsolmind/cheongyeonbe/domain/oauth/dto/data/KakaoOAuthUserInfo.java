package com.catchsolmind.cheongyeonbe.domain.oauth.dto.data;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.config.S3Properties;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/*
 * 카카오 전용 User Info
 */

@RequiredArgsConstructor
public class KakaoOAuthUserInfo {
    private final KakaoUserResponse response;
    private final S3Properties s3Properties;

    private static final String DEFAULT_PROFILE_PATH = "assets/default-profile.png";

    public OAuthUserInfo toOAuthUserInfo() {
        if (response == null || response.kakaoAccount() == null) {
            throw new BusinessException(ErrorCode.KAKAO_SERVER_ERROR);
        }

        String serviceDefaultImageUrl = Optional.ofNullable(s3Properties.getBaseUrl())
                .map(base -> base + "/" + DEFAULT_PROFILE_PATH)
                .orElseThrow(() -> new BusinessException(ErrorCode.S3_CONFIG_ERROR));

        KakaoUserResponse.KakaoAccount.Profile profile = response.kakaoAccount().profile();

        String nickname = "GUEST";
        String finalProfileImg = serviceDefaultImageUrl; // 일단 기본 이미지로 설정

        if (profile != null) {
            if (profile.nickname() != null) {
                nickname = profile.nickname();
            }

            if (profile.profileImageUrl() != null && !Boolean.TRUE.equals(profile.isDefaultImage())) {
                finalProfileImg = profile.profileImageUrl();
            }
        }

        return new OAuthUserInfo(
                AuthProvider.KAKAO,
                response.id(),
                response.kakaoAccount().email(),
                nickname,
                finalProfileImg // 최종 결정된 이미지 URL
        );
    }
}
