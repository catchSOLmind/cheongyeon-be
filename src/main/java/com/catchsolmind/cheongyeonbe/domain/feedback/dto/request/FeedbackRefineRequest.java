package com.catchsolmind.cheongyeonbe.domain.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FeedbackRefineRequest(
        @Schema(description = "원본 문장 리스트")
        @NotEmpty(message = "변환할 문장이 없습니다.")
        @Size(max = 10, message = "한 번에 최대 10개까지 변환 가능합니다.")
        List<String> contents
) {
}
