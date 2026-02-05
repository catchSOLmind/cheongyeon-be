package com.catchsolmind.cheongyeonbe.domain.eraser.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ReservationRequest(
        @Schema(description = "사용할 포인트 (없으면 0, 최대 2만)", example = "5000")
        Integer usedPoint,

        @Schema(description = "예약할 상품 목록")
        @NotEmpty(message = "예약할 상품이 적어도 1개 이상이어야 합니다.")
        @Valid
        List<ReservationItemRequest> reservations
) {
    // 상품별 상세 요청
    @Builder
    public record ReservationItemRequest(
            @Schema(description = "상품 ID", example = "1")
            @NotNull(message = "상품 ID는 필수입니다.")
            Long suggestionTaskId,

            @Schema(description = "방문 날짜", example = "2026-02-05")
            @NotNull(message = "방문 날짜는 필수입니다.")
            @FutureOrPresent(message = "방문 날짜는 현재 또는 미래여야 합니다.")
            LocalDate visitDate,

            @Schema(description = "방문 시간", example = "14:00")
            @NotBlank(message = "방문 시간은 필수입니다.")
            @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "시간 형식은 HH:mm 이어야 합니다.")
            String visitTime,

            @Schema(description = "선택한 옵션")
            @NotNull(message = "옵션 ID는 필수입니다.")
            Long optionId
    ) {
    }
}