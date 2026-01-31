package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.repository.RefreshTokenRepository;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoClientService;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.service.UserService;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.KakaoTokenResponseFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.KakaoUserResponseFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.OAuthUserInfoFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.user.UserDtoFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.entity.UserFixture;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicKakaoAuthServiceTest {
    @InjectMocks
    BasicKakaoAuthService kakaoAuthService;

    @Mock
    private KakaoClientService kakaoClientService;

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("카카오톡 로그인 성공 테스트")
    void loginSuccess() {
        // given
        String authorizationCode = "auth-code";
        String generatedAccessToken = "jwt-access-token";
        String generatedRefreshToken = "jwt-refresh-token";

        long accessExpMs = 3600000L;
        long refreshExpMs = 1209600000L;

        long expectedAccessExpSec = 3600L;
        long expectedRefreshExpSec = 1209600L;

        KakaoTokenResponse tokenResponse = KakaoTokenResponseFixture.valid();
        when(kakaoClientService.requestToken(authorizationCode))
                .thenReturn(tokenResponse);

        KakaoUserResponse kakaoUserResponse = KakaoUserResponseFixture.valid();
        when(kakaoClientService.getKakaoUserInfo("access-token"))
                .thenReturn(kakaoUserResponse);

        OAuthUserInfo oAuthUserInfo = OAuthUserInfoFixture.kakaoUser();
        UserDto mockUserDto = UserDtoFixture.valid();
        when(userService.findOrCreate(any(OAuthUserInfo.class)))
                .thenReturn(mockUserDto);
        User mockUser = UserFixture.base();
        when(userService.findEntityById(anyLong()))
                .thenReturn(mockUser);

        when(jwtProvider.createAccessToken(mockUserDto.userId()))
                .thenReturn(generatedAccessToken);
        when(jwtProvider.createRefreshToken(mockUserDto.userId()))
                .thenReturn(generatedRefreshToken);

        when(jwtProvider.getAccessTokenExpirationMs())
                .thenReturn(accessExpMs);
        when(jwtProvider.getRefreshTokenExpirationMs())
                .thenReturn(refreshExpMs);

        // when
        KakaoLoginResponse response = kakaoAuthService.login(authorizationCode);

        // then
        verify(kakaoClientService).requestToken(authorizationCode);
        verify(kakaoClientService).getKakaoUserInfo(tokenResponse.access_token());

        ArgumentCaptor<OAuthUserInfo> captor = ArgumentCaptor.forClass(OAuthUserInfo.class);
        verify(userService).findOrCreate(captor.capture());

        OAuthUserInfo capturedUserInfo = captor.getValue();
        assertThat(capturedUserInfo.provider()).isEqualTo(AuthProvider.KAKAO);

        assertThat(response).isNotNull();

        assertThat(response.accessToken()).isEqualTo(generatedAccessToken);

        assertThat(response.expiresIn()).isEqualTo(expectedAccessExpSec);

        assertThat(response.refreshToken()).isEqualTo(generatedRefreshToken);
        assertThat(response.refreshTokenExpiresIn()).isEqualTo(expectedRefreshExpSec);

        assertThat(response.user()).usingRecursiveComparison().isEqualTo(mockUserDto);
    }
}