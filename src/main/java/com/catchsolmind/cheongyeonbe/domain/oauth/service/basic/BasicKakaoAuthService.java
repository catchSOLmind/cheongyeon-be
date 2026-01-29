package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.KakaoOAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoClientService;
import com.catchsolmind.cheongyeonbe.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicKakaoAuthService implements KakaoAuthService {
    private final KakaoClientService kakaoClientService;
    private final UserService userService;

    @Override
    public KakaoLoginResponse login(String code) {
        // 인가코드로 카카오 토큰 요청
        KakaoTokenResponse kakaoTokenResponse = kakaoClientService.requestToken(code);

        // 카카오 사용자 정보 조회
        KakaoUserResponse kakaoUserResponse = kakaoClientService.getKakaoUserInfo(kakaoTokenResponse.access_token());

        // 사용자 조회 or 생성
        OAuthUserInfo oAuthUserInfo = new KakaoOAuthUserInfo(kakaoUserResponse).toOAuthUserInfo();
        userService.findOrCreate(oAuthUserInfo);

        // JWT 발급

        return null;
    }
}
