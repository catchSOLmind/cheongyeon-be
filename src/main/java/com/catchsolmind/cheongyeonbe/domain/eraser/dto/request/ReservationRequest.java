package com.catchsolmind.cheongyeonbe.domain.eraser.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ReservationRequest(
        @Schema(description = "사용할 포인트 (없으면 0, 최대 2만)", example = "5000")
        Integer usedPoint,

        @Schema(description = "예약할 상품 목록")
        List<ReservationItemRequest> reservations
) {
    // 상품별 상세 요청
    @Builder
    public record ReservationItemRequest(
            @Schema(description = "상품 ID", example = "1")
            Long suggestionTaskId,

            @Schema(description = "방문 날짜", example = "2026-02-05")
            LocalDate visitDate,

            @Schema(description = "방문 시간", example = "14:00")
            String visitTime,

            @Schema(description = "선택한 옵션")
            Long optionId
    ) {
    }
}