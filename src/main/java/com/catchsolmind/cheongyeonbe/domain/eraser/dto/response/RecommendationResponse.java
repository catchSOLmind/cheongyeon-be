package com.catchsolmind.cheongyeonbe.domain.eraser.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.SuggestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResponse(
        Long suggestionTaskId,

        @Schema(description = "업무 제목")
        String title,

        @Schema(description = "소요 시간(분)")
        Integer defaultEstimatedMinutes,

        @Schema(description = "제공 리워드 포인트")
        Integer rewardPoint,

        @Schema(description = "추천 유형 태그 리스트 " +
                "(DELAYED: 미루어진 작업, NO_ASSIGNEE: 무담당 작업, GENERAL: 시즌 추천, REPEAT: 반복 작업)")
        List<SuggestionType> tags,

        @Schema(description = "추천 이유 설명")
        String description
) {
}
