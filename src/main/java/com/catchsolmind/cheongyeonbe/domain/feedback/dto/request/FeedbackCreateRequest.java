package com.catchsolmind.cheongyeonbe.domain.feedback.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.AiStatus;
import com.catchsolmind.cheongyeonbe.global.enums.PraiseType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record FeedbackCreateRequest(
        @Schema(description = "피드백을 받는 멤버의 ID")
        @NotNull(message = "받는 사람은 필수입니다.")
        Long targetMemberId,

        @Schema(description = "선택한 칭찬 스티커 리스트")
        @NotEmpty(message = "칭찬 스티커는 최소 1개 이상 선택해야 합니다.")
        List<PraiseType> praiseTypes,

        @Schema(description = "개선 피드백 리스트 (선택 사항)")
        @Valid
        List<ImprovementRequest> improvements
) {
    @Builder
    public record ImprovementRequest(
            @Schema(description = "작업 카테고리", example = "BATHROOM")
            @NotNull(message = "카테고리는 필수입니다.")
            TaskCategory category,

            @Schema(description = "사용자가 처음 쓴 글 (필수)")
            @NotNull(message = "내용을 입력해주세요.")
            @Size(min = 1, max = 200, message = "내용은 1자 이상 200자 이하로 작성해야 합니다.")
            String rawText,

            @Schema(description = "AI가 바꿔준 글(없으면 null)")
            String aiText,

            @Schema(description = "FE에서 변환 성공했으면 COMPLETED, 실패했으면 UNCOMPLETED")
            AiStatus aiStatus
    ) {
    }
}
