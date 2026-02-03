package com.catchsolmind.cheongyeonbe.domain.eraser.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record EraserTaskOptionsResponse(
        Long suggestionTaskId,
        String title,
        String imgUrl,
        List<OptionDetail> options
) {
    @Builder
    public record OptionDetail(
            Long optionId,
            String name, // "1개", "2개"
            Integer estimatedMinutes,
            Integer price
    ) {
    }
}
