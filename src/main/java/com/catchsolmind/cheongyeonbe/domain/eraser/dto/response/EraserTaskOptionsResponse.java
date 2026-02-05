package com.catchsolmind.cheongyeonbe.domain.eraser.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record EraserTaskOptionsResponse(
        Long suggestionTaskId,

        @Schema(description = "추천 업무 이름", example = "냉장실 청소")
        String title,

        @Schema(description = "이미지 링크")
        String imgUrl,

        List<OptionDetail> options
) {
    @Builder
    public record OptionDetail(
            Long optionId,

            @Schema(description = "개수")
            String count,

            @Schema(description = "소요 시간(분)")
            Integer estimatedMinutes,

            @Schema(description = "가격")
            Integer price
    ) {
    }
}
