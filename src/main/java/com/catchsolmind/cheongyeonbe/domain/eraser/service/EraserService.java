package com.catchsolmind.cheongyeonbe.domain.eraser.service;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.RecommendationResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.SuggestionTask;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.SuggestionTaskRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskLogRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.SuggestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EraserService {

    private final GroupMemberRepository groupMemberRepository;
    private final TaskOccurrenceRepository taskOccurrenceRepository;
    private final SuggestionTaskRepository suggestionTaskRepository;
    private final TaskLogRepository taskLogRepository;

    public List<RecommendationResponse> getRecommendations(Long userId) {
        // 1. 유저의 그룹 찾기
        Group group = groupMemberRepository.findGroupByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        // 2. 데이터 조회
        List<TaskOccurrence> unfinishedTasks = taskOccurrenceRepository.findUnfinishedByGroupId(group.getGroupId());
        List<SuggestionTask> products = suggestionTaskRepository.findAll();

        List<RecommendationResponse> recommendations = new ArrayList<>();

        // 3. 상품별 추천 여부 검사
        for (SuggestionTask product : products) {
            TaskOccurrence matchedOccurrence = unfinishedTasks.stream()
                    .filter(o -> o.getTask().getTaskType().equals(product.getTaskType()))
                    .findFirst()
                    .orElse(null);

            boolean isSeason = isSeasonMatch(product);
            boolean shouldRecommend = false;
            List<SuggestionType> currentTags = new ArrayList<>();
            String description = "";

            // [Case A] 일정에 있는 경우 (미루기 감지)
            if (matchedOccurrence != null) {
                boolean isTooManyPostpones = matchedOccurrence.getPostponeCount() >= 3;
                long overdueDays = ChronoUnit.DAYS.between(matchedOccurrence.getOccurDate(), LocalDate.now());
                boolean isLongOverdue = overdueDays >= 7;

                if (isTooManyPostpones || isLongOverdue) {
                    shouldRecommend = true;
                    currentTags.add(SuggestionType.DELAYED);

                    // 멘트: 미루기 템플릿 적용
                    String period = overdueDays >= 14 ? "2주" : (overdueDays + "일");
                    String time = (product.getDefaultEstimatedMinutes() / 60) + "시간";
                    description = product.getDescDelayed()
                            .replace("{delay_period}", period)
                            .replace("{time}", time);
                }

                if (isSeason) {
                    currentTags.add(SuggestionType.GENERAL);
                }
            }

            // [Case B] 일정에 없는 경우 (주기/시즌 체크)
            else {
                // 1. 주기가 설정된 상품인지 확인
                if (product.getRecommendationCycleDays() != null) {
                    LocalDateTime lastDoneAt = taskLogRepository.findLastDoneDate(
                            group.getGroupId(),
                            product.getTaskType().getTaskTypeId()
                    );

                    // 마지막 기록을 기준으로 경과일 계산
                    long daysSinceLast = lastDoneAt == null ?
                            9999 : ChronoUnit.DAYS.between(lastDoneAt.toLocalDate(), LocalDate.now());

                    // 주기가 지났으면 무담당 작업
                    if (daysSinceLast >= product.getRecommendationCycleDays()) {
                        shouldRecommend = true;
                        currentTags.add(SuggestionType.NO_ASSIGNEE);

                        // 멘트: 무담당 템플릿 적용
                        description = product.getDescNoAssignee()
                                .replace("{task_name}", product.getTitle())
                                .replace("{season}", getCurrentSeasonName());

                        // (선택) 시즌이면 시즌 태그도 같이 붙여줌
                        if (isSeason) {
                            currentTags.add(SuggestionType.GENERAL);
                        }
                    }
                }
                // 2. 주기는 없지만(혹은 안 지났지만) 시즌 상품인 경우 -> 시즌 추천만 띄움
                else if (isSeason) {
                    shouldRecommend = true;
                    currentTags.add(SuggestionType.GENERAL);
                    description = product.getDescGeneral()
                            .replace("{season}", getCurrentSeasonName());
                }
            }

            // [결과] 추천 대상이면 리스트 추가
            if (shouldRecommend) {
                recommendations.add(RecommendationResponse.builder()
                        .suggestionTaskId(product.getSuggestionTaskId())
                        .title(product.getTitle())
                        .defaultEstimatedMinutes(product.getDefaultEstimatedMinutes())
                        .rewardPoint(product.getRewardPoint())
                        .tags(currentTags)
                        .description(description)
                        .build());
            }
        } // end for

        return recommendations; // [수정] for문 밖으로 이동
    }

    // 헬퍼 메서드
    private boolean isSeasonMatch(SuggestionTask task) {
        if (task.getSeasonMonths() == null) return false;
        int currentMonth = LocalDate.now().getMonthValue();
        String[] months = task.getSeasonMonths().split(",");
        for (String m : months) {
            if (Integer.parseInt(m.trim()) == currentMonth) return true;
        }
        return false;
    }

    private String getCurrentSeasonName() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 3 && month <= 5) return "봄";
        if (month >= 6 && month <= 8) return "여름";
        if (month >= 9 && month <= 11) return "가을";
        return "겨울";
    }
}