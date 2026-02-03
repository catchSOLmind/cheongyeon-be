package com.catchsolmind.cheongyeonbe.domain.houseworktest.controller;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.ChoiceResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.service.HouseworkTestService;
import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = HouseworkTestController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
class HouseworkTestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HouseworkTestService houseworkTestService;

    @MockBean
    private com.catchsolmind.cheongyeonbe.global.security.jwt.JwtProvider jwtProvider;
    @MockBean
    private com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetailsService jwtUserDetailsService;

    @Test
    @DisplayName("가사성향테스트 질문 목록 조회")
    void getListOfGetQuestions() throws Exception {
        // given
        HouseworkTestQuestionResponse q1 =
                HouseworkTestQuestionResponse.builder()
                        .questionId(1L)
                        .order(1)
                        .content("질문1")
                        .choices(List.of(
                                ChoiceResponse.builder()
                                        .choiceType(ChoiceType.A)
                                        .content("선택지 A")
                                        .build(),
                                ChoiceResponse.builder()
                                        .choiceType(ChoiceType.B)
                                        .content("선택지 B")
                                        .build()
                        ))
                        .build();

        HouseworkTestQuestionsResponse response =
                HouseworkTestQuestionsResponse.of(List.of(q1));

        given(houseworkTestService.getQuestions())
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/housework-test/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.result.questions").isArray())
                .andExpect(jsonPath("$.result.questions[0].questionId").value(1))
                .andExpect(jsonPath("$.result.questions[0].order").value(1))
                .andExpect(jsonPath("$.result.questions[0].choices[0].choiceType").value("A"));
    }
}