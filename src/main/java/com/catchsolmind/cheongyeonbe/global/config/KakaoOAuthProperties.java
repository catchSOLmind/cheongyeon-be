package com.catchsolmind.cheongyeonbe.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kakao.oauth")
public class KakaoOAuthProperties {

    private String tokenUri;

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String userInfoUri;
}
