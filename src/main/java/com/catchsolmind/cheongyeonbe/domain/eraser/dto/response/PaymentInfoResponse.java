package com.catchsolmind.cheongyeonbe.domain.eraser.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PaymentInfoResponse(
        @Schema(description = "보유 포인트")
        Integer currentPoint,

        @Schema(description = "사용 가능 포인트(2만 포인트 제한)")
        Integer maxUsablePoint
) {
}
