package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.global.config.KakaoOAuthProperties;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.KakaoTokenResponseFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.KakaoUserResponseFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicKakaoClientServiceTest {
    @InjectMocks
    BasicKakaoClientService kakaoClientService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private KakaoOAuthProperties kakaoOAuthProperties;

    @Test
    @DisplayName("인가코드로 카카오 토큰을 요청한다.")
    void tokenRequestWithAuthorizationCode() {
        // given
        String code = "kakao-auth-code";
        KakaoTokenResponse response = KakaoTokenResponseFixture.valid();

        when(kakaoOAuthProperties.getTokenUri()).thenReturn("https://kauth.kakao.com/oauth/token");
        when(kakaoOAuthProperties.getClientId()).thenReturn("client-id");
        when(kakaoOAuthProperties.getRedirectUri()).thenReturn("redirect-uri");

        when(restTemplate.postForEntity(
                anyString(), // url
                any(HttpEntity.class), // request(요청 바디+헤더)
                eq(KakaoTokenResponse.class) // responseType(응답을 매핑할 클래스)
        )).thenReturn(ResponseEntity.ok(response));

        // when
        KakaoTokenResponse result = kakaoClientService.requestToken(code);

        // then
        assertThat(result.token_type()).isEqualTo("bearer");
        assertThat(result.access_token()).isEqualTo("access-token");
        assertThat(result.expires_in()).isEqualTo(21599);
        assertThat(result.refresh_token()).isEqualTo("refresh-token");
        assertThat(result.refresh_token_expires_in()).isEqualTo(5183999);
    }

    @Test
    @DisplayName("카카오 토큰으로 카카오 사용자 정보를 조회한다.")
    void getKakaoUserInfoUsingKakaoAccessToken() {
        // given
        String accessToken = KakaoTokenResponseFixture.valid().access_token();
        KakaoUserResponse response = KakaoUserResponseFixture.valid();

        when(kakaoOAuthProperties.getUserInfoUri()).thenReturn("https://kapi.kakao.com/v2/user/me");
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(KakaoUserResponse.class)
        )).thenReturn(ResponseEntity.ok(response));

        // when
        KakaoUserResponse result = kakaoClientService.getKakaoUserInfo(accessToken);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.kakaoAccount().profile().nickname()).isEqualTo("유저1 닉네임");
    }
}