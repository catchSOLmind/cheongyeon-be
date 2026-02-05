package com.catchsolmind.cheongyeonbe.domain.eraser.service;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.EraserTaskOptionsResponse;
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
import com.catchsolmind.cheongyeonbe.global.config.S3Properties;
import com.catchsolmind.cheongyeonbe.global.enums.SuggestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EraserService {

    private final GroupMemberRepository groupMemberRepository;
    private final TaskOccurrenceRepository taskOccurrenceRepository;
    private final SuggestionTaskRepository suggestionTaskRepository;
    private final TaskLogRepository taskLogRepository;
    private final S3Properties s3Properties;

    public List<RecommendationResponse> getRecommendations(Long userId) {
        // 1. 유저의 그룹 찾기
        Group group = groupMemberRepository.findGroupByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        // 2. 데이터 조회
        List<TaskOccurrence> unfinishedTasks = taskOccurrenceRepository.findUnfinishedByGroupId(group.getGroupId());
        List<SuggestionTask> products = suggestionTaskRepository.findAll();

        List<RecommendationResponse> recommendations = new ArrayList<>();

        // 2-1. 상품들의 TaskTypeId 목록 추출
        List<Long> productTaskTypeIds = products.stream()
                .map(p -> p.getTaskType().getTaskTypeId())
                .toList();

        // 2-2. 해당 TaskType들에 대한 마지막 수행일 일괄 조회 (쿼리 1번 실행)
        List<Object[]> lastDoneLogs = taskLogRepository.findLastDoneDatesByGroupAndTaskTypes(group.getGroupId(), productTaskTypeIds);

        // 2-3. 조회 결과를 Map으로 변환 (Key: TaskTypeId, Value: LastDoneDate) -> O(1) 검색 속도
        Map<Long, LocalDateTime> lastDoneMap = lastDoneLogs.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (LocalDateTime) row[1]
                        // 주의: H2나 MySQL 버전에 따라 Timestamp로 반환될 수 있으므로,
                        // ClassCastException 발생 시 ((Timestamp) row[1]).toLocalDateTime() 으로 변경 필요
                ));

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
                    LocalDateTime lastDoneAt = lastDoneMap.get(product.getTaskType().getTaskTypeId());

                    // 마지막 기록을 기준으로 경과일 계산
                    long daysSinceLast = lastDoneAt == null ?
                            9999 : ChronoUnit.DAYS.between(lastDoneAt.toLocalDate(), LocalDate.now());

                    // 주기가 지났으면 무담당 작업
                    if (daysSinceLast >= product.getRecommendationCycleDays()) {
                        shouldRecommend = true;
                        currentTags.add(SuggestionType.NO_ASSIGNEE);

                        description = product.getDescNoAssignee()
                                .replace("{task_name}", product.getTitle())
                                .replace("{season}", getCurrentSeasonName());

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
                String fullImgUrl = Optional.ofNullable(s3Properties.getBaseUrl())
                        .map(base -> base + "/" + product.getImgUrl())
                        .orElseThrow(() -> new BusinessException(ErrorCode.S3_CONFIG_ERROR));

                recommendations.add(RecommendationResponse.builder()
                        .suggestionTaskId(product.getSuggestionTaskId())
                        .title(product.getTitle())
                        .imgUrl(fullImgUrl)
                        .defaultEstimatedMinutes(product.getDefaultEstimatedMinutes())
                        .rewardPoint(product.getRewardPoint())
                        .tags(currentTags)
                        .description(description)
                        .build());
            }
        }

        return recommendations.stream()
                .sorted((r1, r2) -> {
                    // 우선순위 점수 계산 (낮을수록 높음)
                    int score1 = getPriorityScore(r1.tags());
                    int score2 = getPriorityScore(r2.tags());
                    return Integer.compare(score1, score2);
                })
                .limit(3) // 최대 3개까지만 자름
                .collect(Collectors.toList());
    }

    public List<EraserTaskOptionsResponse> getTaskOptions(
            List<Long> suggestionTaskId,
            Long userId
    ) {
        // 요청 suggestionTaskId로 DB(SuggestionTask)를 조회해서 해당 상품들 한 번에 조회 (findAllById)
        if (suggestionTaskId == null || suggestionTaskId.isEmpty()) {
            return List.of();
        }

        List<SuggestionTask> tasks = suggestionTaskRepository.findAllById(suggestionTaskId);

        return tasks.stream()
                .map(task -> {
                    // 이미지 URL
                    String fullImgUrl = Optional.ofNullable(s3Properties.getBaseUrl())
                            .map(base -> base + "/" + task.getImgUrl())
                            .orElseThrow(() -> new BusinessException(ErrorCode.S3_CONFIG_ERROR));

                    // 옵션 매핑 
                    List<EraserTaskOptionsResponse.OptionDetail> optionDetails = task.getOptions().stream()
                            .map(opt -> new EraserTaskOptionsResponse.OptionDetail(
                                    opt.getOptionId(),
                                    opt.getCount(),
                                    opt.getEstimatedMinutes(),
                                    opt.getPrice()
                            ))
                            .collect(Collectors.toList());

                    return EraserTaskOptionsResponse.builder()
                            .suggestionTaskId(task.getSuggestionTaskId())
                            .title(task.getTitle())
                            .imgUrl(fullImgUrl)
                            .options(optionDetails)
                            .build();
                })
                .collect(Collectors.toList());
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

    private int getPriorityScore(List<SuggestionType> tags) {
        if (tags.contains(SuggestionType.DELAYED)) return 1;      // 1순위: 미룬 것
        if (tags.contains(SuggestionType.NO_ASSIGNEE)) return 2;  // 2순위: 담당자 없는 것
        return 3;                                                 // 3순위: 시즌 추천 등
    }
}