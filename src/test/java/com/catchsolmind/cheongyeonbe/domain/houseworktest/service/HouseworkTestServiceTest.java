package com.catchsolmind.cheongyeonbe.domain.houseworktest.service;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request.HouseworkTestAnswerRequest;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request.HouseworkTestSubmitRequest;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestResultResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestChoice;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestQuestion;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestChoiceRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestQuestionRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
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

    @Test
    @DisplayName("정상적으로 테스트 결과 계산")
    void submitTestSuccess() {
        // given
        HouseworkTestQuestion q1 = HouseworkTestQuestion.builder()
                .questionId(1L)
                .build();

        HouseworkTestChoice choiceA = HouseworkTestChoice.builder()
                .question(q1)
                .choiceType(ChoiceType.A)
                .build();

        given(questionRepository.findAllById(List.of(1L)))
                .willReturn(List.of(q1));

        given(choiceRepository.findAllByQuestionIn(List.of(q1)))
                .willReturn(List.of(choiceA));

        HouseworkTestSubmitRequest request =
                new HouseworkTestSubmitRequest(
                        List.of(new HouseworkTestAnswerRequest(1L, ChoiceType.A))
                );

        // when
        HouseworkTestResultResponse response =
                houseworkTestService.submitTest(request, null);

        // then
        assertThat(response.resultType()).isEqualTo(TestResultType.RELAXED);
    }

    @Test
    @DisplayName("존재하지 않는 질문에 답변하면 예외")
    void submitTestWhenInvalidQuestion() {
        given(questionRepository.findAllById(List.of(1L)))
                .willReturn(List.of());

        HouseworkTestSubmitRequest request =
                new HouseworkTestSubmitRequest(
                        List.of(new HouseworkTestAnswerRequest(1L, ChoiceType.A))
                );

        assertThatThrownBy(() -> houseworkTestService.submitTest(request, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.QUESTION_NOT_FOUND);
    }

    @Test
    @DisplayName("질문에 없는 선택지를 고르면 예외")
    void submitTestWhenInvalidChoice() {
        HouseworkTestQuestion q1 = HouseworkTestQuestion.builder()
                .questionId(1L)
                .build();

        given(questionRepository.findAllById(List.of(1L)))
                .willReturn(List.of(q1));

        given(choiceRepository.findAllByQuestionIn(List.of(q1)))
                .willReturn(List.of()); // 선택지 없음

        HouseworkTestSubmitRequest request =
                new HouseworkTestSubmitRequest(
                        List.of(new HouseworkTestAnswerRequest(1L, ChoiceType.A))
                );

        assertThatThrownBy(() -> houseworkTestService.submitTest(request, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CHOICE_NOT_FOUND);
    }

}