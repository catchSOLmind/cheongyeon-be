package com.catchsolmind.cheongyeonbe.domain.houseworktest.service;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request.HouseworkTestAnswerRequest;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request.HouseworkTestSubmitRequest;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestResultResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestChoice;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestQuestion;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestChoiceRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestQuestionRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestResultRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HouseworkTestService {
    private final HouseworkTestQuestionRepository questionRepository;
    private final HouseworkTestChoiceRepository choiceRepository;
    private final HouseworkTestResultRepository resultRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public HouseworkTestResultResponse submitTest(
            HouseworkTestSubmitRequest request,
            Long userId
    ) {
        // 요청 검증
        if (request.answers() == null || request.answers().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_CHOICE);
        }

        if (request.answers().stream()
                .anyMatch(a -> a.questionId() == null || a.choiceType() == null)
        ) {
            throw new BusinessException(ErrorCode.INVALID_CHOICE);
        }

        // 질문 조회
        List<Long> questionIds = request.answers().stream()
                .map(HouseworkTestAnswerRequest::questionId)
                .toList();

        Set<Long> uniqueQuestionIds = questionIds.stream()
                .collect(Collectors.toSet());
        if (uniqueQuestionIds.size() != questionIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_CHOICE);
        }

        long totalQuestions = questionRepository.count();
        if (uniqueQuestionIds.size() != totalQuestions) {
            throw new BusinessException(ErrorCode.INVALID_CHOICE);
        }

        List<HouseworkTestQuestion> questions =
                questionRepository.findAllById(questionIds);

        if (questions.size() != questionIds.size()) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        // 선택지 조회
        List<HouseworkTestChoice> choices =
                choiceRepository.findAllByQuestionIn(questions);

        if (choices.isEmpty()) {
            throw new BusinessException(ErrorCode.CHOICE_NOT_FOUND);
        }

        // 답변 검증 + 점수 계산
        Map<Long, Map<ChoiceType, HouseworkTestChoice>> choiceMap =
                choices.stream()
                        .collect(Collectors.groupingBy(
                                c -> c.getQuestion().getQuestionId(),
                                Collectors.toMap(
                                        HouseworkTestChoice::getChoiceType,
                                        c -> c
                                )
                        ));

        int active = 0;
        int clean = 0;
        int routine = 0;
        int sloppy = 0;

        for (HouseworkTestAnswerRequest answer : request.answers()) {
            Map<ChoiceType, HouseworkTestChoice> questionChoices =
                    choiceMap.get(answer.questionId());

            if (questionChoices == null) {
                throw new BusinessException(ErrorCode.INVALID_CHOICE);
            }

            HouseworkTestChoice choice =
                    questionChoices.get(answer.choiceType());

            if (choice == null) {
                throw new BusinessException(ErrorCode.INVALID_CHOICE);
            }

            active += choice.getActiveScore();
            clean += choice.getCleanScore();
            routine += choice.getRoutineScore();
            sloppy += choice.getSloppyScore();
        }

        // 결과 계산
        TestResultType resultType = calculateType(active, clean, routine, sloppy);

        List<Integer> finalScores = List.of(
                convertToDisplayScore(clean),
                convertToDisplayScore(active),
                convertToDisplayScore(routine),
                convertToDisplayScore(sloppy)
        );

        // 로그인 사용자는 결과 저장
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            saveOrUpdateResult(user, resultType);
        }

        return HouseworkTestResultResponse.of(resultType, finalScores);
    }

    private TestResultType calculateType(int active, int clean, int routine, int sloppy) {
        // 1. 내일이(PROCRASTINATOR) 체크: 주도성과 계획성이 낮고 수용성이 높은 경우
        if (active < 0 && routine < 0 && sloppy > 0) return TestResultType.PROCRASTINATOR;

        // 2. 뽀득이(PERFECTIONIST) 체크: 모든 지표가 꼼꼼하고 주도적인 경우
        if (clean > 2 && active > 0 && routine > 0) return TestResultType.PERFECTIONIST;

        // 3. 효율이(EFFICIENT) 체크: 계획성은 높으나 수용성(적당히 함)이 있는 경우
        if (routine > 0 && sloppy >= 0) return TestResultType.EFFICIENT;

        // 4. 그 외 기본값은 느긋이(RELAXED)
        return TestResultType.RELAXED;
    }

    private void saveOrUpdateResult(User user, TestResultType resultType) {
        HouseworkTestResult result =
                resultRepository.findByUser(user)
                        .orElse(
                                HouseworkTestResult.builder()
                                        .user(user)
                                        .build()
                        );

        result.changeResultType(resultType);
        resultRepository.save(result);
    }

    private int convertToDisplayScore(int rawScore) {
        int calculated = 50 + (rawScore * 2);

        return Math.max(5, Math.min(100, calculated));
    }
}
