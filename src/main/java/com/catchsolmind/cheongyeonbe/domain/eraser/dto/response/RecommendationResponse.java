package com.catchsolmind.cheongyeonbe.domain.eraser.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.SuggestionType;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResponse(
        Long suggestionTaskId,
        String title,
        Integer defaultEstimatedMinutes,
        Integer rewardPoint,
        List<SuggestionType> tags,
        String description
) {
}
