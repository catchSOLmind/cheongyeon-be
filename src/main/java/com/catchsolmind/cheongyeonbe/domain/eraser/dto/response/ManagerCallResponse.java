package com.catchsolmind.cheongyeonbe.domain.eraser.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "매니저 호출(예약) 정보")
public record ManagerCallResponse(
        @Schema(description = "예약 ID")
        Long reservationItemId,

        @Schema(description = "서비스명 (예: 주방 청소)")
        String serviceName,

        @Schema(description = "방문 시간 (예: 14:00)")
        String visitTime,

        @Schema(description = "얻을 포인트")
        Integer point
) {
}
