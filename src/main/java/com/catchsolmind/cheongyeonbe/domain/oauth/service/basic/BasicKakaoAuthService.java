package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.KakaoOAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.entity.RefreshToken;
import com.catchsolmind.cheongyeonbe.domain.oauth.repository.RefreshTokenRepository;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoClientService;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.mapper.UserMapper;
import com.catchsolmind.cheongyeonbe.domain.user.service.UserService;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicKakaoAuthService implements KakaoAuthService {
    private final KakaoClientService kakaoClientService;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;

    @Override
    public KakaoLoginResponse login(String code, String redirectUri) {
        String finalRedirectUri = (redirectUri != null && !redirectUri.isBlank())
                ? redirectUri
                : kakaoClientService.getDefaultRedirectUri();

        // 인가코드로 카카오 토큰 요청
        KakaoTokenResponse kakaoTokenResponse = kakaoClientService.requestToken(code, finalRedirectUri);

        // 카카오 사용자 정보 조회
        KakaoUserResponse kakaoUserResponse = kakaoClientService.getKakaoUserInfo(kakaoTokenResponse.access_token());

        // 사용자 조회 or 생성
        OAuthUserInfo oAuthUserInfo = new KakaoOAuthUserInfo(kakaoUserResponse).toOAuthUserInfo();
        UserDto userDto = userService.findOrCreate(oAuthUserInfo);
        User user = userService.findEntityById(userDto.userId());

        // JWT 발급
        String accessToken = jwtProvider.createAccessToken(userDto.userId());
        String refreshToken = jwtProvider.createRefreshToken(userDto.userId());

        refreshTokenRepository.deleteByUser_UserId(user.getUserId());
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .refreshToken(refreshToken)
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusSeconds(
                                jwtProvider.getRefreshTokenExpirationMs() / 1000
                        ))
                        .build()
        );

        return KakaoLoginResponse.of(userDto,
                accessToken,
                jwtProvider.getAccessTokenExpirationMs(),
                refreshToken,
                jwtProvider.getRefreshTokenExpirationMs()
        );
    }
}
