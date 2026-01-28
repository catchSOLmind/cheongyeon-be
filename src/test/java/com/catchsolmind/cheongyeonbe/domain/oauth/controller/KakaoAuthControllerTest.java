package com.catchsolmind.cheongyeonbe.domain.oauth.controller;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.KakaoAuthService;
import com.catchsolmind.cheongyeonbe.global.fixture.oauth.KakaoLoginResponseFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KakaoAuthController.class)
class KakaoAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoAuthService kakaoAuthService;

    @Test
    @WithMockUser
    @DisplayName("카카오 인가코드를 받아서 로그인 API를 호출한다.")
    void GetTheAuthorizationCodeAndRequestTheToken() throws Exception {
        // given
        String authorizationCode = "kakao-auth-code";

        KakaoLoginResponse response = KakaoLoginResponseFixture.valid();

        Mockito.when(kakaoAuthService.login(anyString()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        post("/oauth/kakao/login")
                                .with(csrf())
                                .param("code", authorizationCode)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.nickname").value("유저1"))
                .andExpect(jsonPath("$.profileImg").value("profile-img-url"));
    }
}