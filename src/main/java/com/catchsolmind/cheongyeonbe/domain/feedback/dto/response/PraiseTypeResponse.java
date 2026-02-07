package com.catchsolmind.cheongyeonbe.domain.feedback.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.PraiseType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PraiseTypeResponse(
        @Schema(description = "서버로 보낼 Enum 코드", example = "DETAIL_KING")
        String code,

        @Schema(description = "화면에 보일 제목", example = "꼼꼼왕")
        String title,

        @Schema(description = "화면에 보일 설명", example = "꼼꼼하게 잘 해요")
        String description
) {
    public static PraiseTypeResponse from(PraiseType praiseType) {
        return new PraiseTypeResponse(
                praiseType.name(),
                praiseType.getTitle(),
                praiseType.getDescription()
        );
    }
}
