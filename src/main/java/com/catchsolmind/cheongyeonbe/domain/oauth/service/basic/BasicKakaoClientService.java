package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoClientService;
import com.catchsolmind.cheongyeonbe.global.config.KakaoOAuthProperties;
import com.catchsolmind.cheongyeonbe.global.exception.oauth.KakaoServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BasicKakaoClientService implements KakaoClientService {
    private final RestTemplate restTemplate; // TODO: RestClient로 리팩토링
    private final KakaoOAuthProperties kakaoOAuthProperties;

    @Override
    public KakaoTokenResponse requestToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code"); // 카카오 고정
            body.add("client_id", kakaoOAuthProperties.getClientId());
            body.add("client_secret", kakaoOAuthProperties.getClientSecret());
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
        } catch (ResourceAccessException e) {
            throw new KakaoServerException();
        }
    }

    @Override
    public KakaoUserResponse getKakaoUserInfo(String accessToken) {
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
}
