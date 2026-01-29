package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoClientService;
import com.catchsolmind.cheongyeonbe.domain.user.service.UserService;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.KakaoTokenResponseFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.KakaoUserResponseFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("카카오톡 로그인 성공 테스트")
    void loginSuccess() {
        // given
        String authorizationCode = "auth-code";

        KakaoTokenResponse tokenResponse = KakaoTokenResponseFixture.valid();
        KakaoUserResponse kakaoUserResponse = KakaoUserResponseFixture.valid();
        when(kakaoClientService.requestToken(authorizationCode))
                .thenReturn(tokenResponse);
        when(kakaoClientService.getKakaoUserInfo("access-token"))
                .thenReturn(kakaoUserResponse);

        // when
        kakaoAuthService.login(authorizationCode);

        // then
        verify(kakaoClientService).requestToken(authorizationCode);
        verify(kakaoClientService).getKakaoUserInfo("access-token");

        ArgumentCaptor<OAuthUserInfo> captor = ArgumentCaptor.forClass(OAuthUserInfo.class);
        verify(userService).findOrCreate(captor.capture());

        OAuthUserInfo userInfo = captor.getValue();
        assertThat(userInfo.provider()).isEqualTo(AuthProvider.KAKAO);
        assertThat(userInfo.providerId()).isEqualTo(1L);
        assertThat(userInfo.email()).isEqualTo("email@email.com");
        assertThat(userInfo.nickname()).isEqualTo("nickname");
    }
}