package com.catchsolmind.cheongyeonbe.domain.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record FeedbackRefineResponse(
        @Schema(description = "변환된 문장 리스트 (순서 보장)")
        List<String> refinedContents
) {
}
