package com.catchsolmind.cheongyeonbe.domain.houseworktest.service;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestChoice;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestQuestion;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestChoiceRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestQuestionRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HouseworkTestServiceTest {
    @InjectMocks
    private HouseworkTestService houseworkTestService;

    @Mock
    private HouseworkTestQuestionRepository questionRepository;

    @Mock
    private HouseworkTestChoiceRepository choiceRepository;

    @Test
    @DisplayName("정상적으로 질문 + 선택지 조회")
    void getQuestionsSuccess() {
        // given
        HouseworkTestQuestion question = HouseworkTestQuestion.builder()
                .questionId(1L)
                .questionOrder(1)
                .content("질문")
                .build();

        HouseworkTestChoice choiceA = HouseworkTestChoice.builder()
                .question(question)
                .choiceType(ChoiceType.A)
                .content("선택지 A")
                .build();

        HouseworkTestChoice choiceB = HouseworkTestChoice.builder()
                .question(question)
                .choiceType(ChoiceType.B)
                .content("선택지 B")
                .build();

        given(questionRepository.findAllByOrderByQuestionOrderAsc())
                .willReturn(List.of(question));

        given(choiceRepository.findAllByQuestionIn(List.of(question)))
                .willReturn(List.of(choiceA, choiceB));

        // when
        HouseworkTestQuestionsResponse response =
                houseworkTestService.getQuestions();

        // then
        assertThat(response.questions()).hasSize(1);
        assertThat(response.questions().get(0).choices()).hasSize(2);
    }


    @Test
    @DisplayName("질문이 하나도 없으면 TEST001 예외 발생")
    void getQuestionsWhenNoQuestions() {
        // given
        given(questionRepository.findAllByOrderByQuestionOrderAsc())
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> houseworkTestService.getQuestions())
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.QUESTION_NOT_FOUND);
    }

    @Test
    @DisplayName("질문은 있으나 선택지가 없으면 TEST002 예외 발생")
    void getQuestionsWhenNoChoices() {
        // given
        HouseworkTestQuestion question = HouseworkTestQuestion.builder()
                .questionId(1L)
                .questionOrder(1)
                .content("질문")
                .build();

        given(questionRepository.findAllByOrderByQuestionOrderAsc())
                .willReturn(List.of(question));

        given(choiceRepository.findAllByQuestionIn(List.of(question)))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> houseworkTestService.getQuestions())
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CHOICE_NOT_FOUND);
    }

}