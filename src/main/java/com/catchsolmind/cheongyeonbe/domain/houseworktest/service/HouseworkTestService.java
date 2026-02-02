package com.catchsolmind.cheongyeonbe.domain.houseworktest.service;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestChoice;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestQuestion;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestChoiceRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestQuestionRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HouseworkTestService {
    private final HouseworkTestQuestionRepository questionRepository;
    private final HouseworkTestChoiceRepository choiceRepository;

    public HouseworkTestQuestionsResponse getQuestions() {
        List<HouseworkTestQuestion> questions = questionRepository.findAllByOrderByQuestionOrderAsc();
        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_CHOICE);
        }

        List<HouseworkTestQuestionResponse> responses =
                questions.stream()
                        .map(question -> {
                            List<HouseworkTestChoice> choices =
                                    choiceRepository.findAllByQuestion(question);

                            if (choices.isEmpty()) {
                                throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
                            }

                            return HouseworkTestQuestionResponse.from(question, choices);
                        })
                        .toList();
        return HouseworkTestQuestionsResponse.of(responses);
    }
}
