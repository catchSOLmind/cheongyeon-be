package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicKakaoAuthServiceTest {
    @InjectMocks
    BasicKakaoAuthService kakaoAuthService;

    @Test
    @DisplayName("카카오 사용자가 우리 서비스에 있는지 확인하고 없으면 생성한다.")
    void verifyKakaoUserAndCreateIfNotExist() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("JWT를 발급한다.")
    void generateJwt() {
    }
}