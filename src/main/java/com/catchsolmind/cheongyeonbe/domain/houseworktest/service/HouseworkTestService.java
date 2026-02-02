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
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        List<HouseworkTestChoice> allChoices =
                choiceRepository.findAllByQuestionIn(questions);

        Map<Long, List<HouseworkTestChoice>> choiceMap =
                allChoices.stream()
                        .collect(Collectors.groupingBy(
                                choice -> choice.getQuestion().getQuestionId()
                        ));

        List<HouseworkTestQuestionResponse> responses =
                questions.stream()
                        .map(question -> {
                            List<HouseworkTestChoice> choices =
                                    choiceMap.get(question.getQuestionId());

                            if (choices == null || choices.isEmpty()) {
                                throw new BusinessException(ErrorCode.CHOICE_NOT_FOUND);
                            }

                            return HouseworkTestQuestionResponse.from(question, choices);
                        })
                        .toList();

        return HouseworkTestQuestionsResponse.of(responses);
    }
}
