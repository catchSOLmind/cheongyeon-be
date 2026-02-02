package com.catchsolmind.cheongyeonbe.domain.oauth.dto.data;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import lombok.RequiredArgsConstructor;

/*
 * 카카오 전용 User Info
 */

@RequiredArgsConstructor
public class KakaoOAuthUserInfo {
    private final KakaoUserResponse response;

    public OAuthUserInfo toOAuthUserInfo() {
        if (response == null ||
                response.kakaoAccount() == null ||
                response.kakaoAccount().profile() == null) {
            throw new BusinessException(ErrorCode.KAKAO_SERVER_ERROR);
        }

        return new OAuthUserInfo(
                AuthProvider.KAKAO,
                response.id(),
                response.kakaoAccount().email(),
                response.kakaoAccount().profile().nickname(),
                response.kakaoAccount().profile().profileImageUrl()
        );
    }
}
