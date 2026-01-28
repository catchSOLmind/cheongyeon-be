package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
import com.catchsolmind.cheongyeonbe.global.config.KakaoOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BasicKakaoAuthService implements KakaoAuthService {
    private final RestTemplate restTemplate; // TODO: RestClient로 리팩토링
    private final KakaoOAuthProperties kakaoOAuthProperties;


    @Override
    public KakaoLoginResponse login(String code) {
        // 인가코드로 카카오 토큰 요청
        KakaoTokenResponse kakaoTokenResponse = requestToken(code);

        // 카카오 사용자 정보 조회
        KakaoUserResponse kakaoUserResponse = getKakaoUserInfo(kakaoTokenResponse.access_token());

        // 사용자 조회 or 생성

        // JWT 발급

        return null;
    }

    KakaoUserResponse getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response =
                restTemplate.exchange(
                        kakaoOAuthProperties.getUserInfoUri(),
                        HttpMethod.GET,
                        request,
                        KakaoUserResponse.class
                );

        return response.getBody();
    }

    KakaoTokenResponse requestToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code"); // 카카오 고정
        body.add("client_id", kakaoOAuthProperties.getClientId());
        body.add("redirect_uri", kakaoOAuthProperties.getRedirectUri());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenResponse> response =
                restTemplate.postForEntity(
                        kakaoOAuthProperties.getTokenUri(),
                        request,
                        KakaoTokenResponse.class
                );

        return response.getBody();
    }
}
