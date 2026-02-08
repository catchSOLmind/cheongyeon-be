package com.catchsolmind.cheongyeonbe.domain.feedback.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record ReportResponse(
        @Schema(description = "리포트 기간", example = "2026년 1월 1주차")
        String period,

        @Schema(description = "이번 주 우리 그룹 타이틀", example = "완벽주의 팀플러")
        String groupTitle,

        @Schema(description = "이번 주 리포트 3줄 요약")
        List<String> summaries,

        @Schema(description = "내가 받은 칭찬 스탬프 목록")
        List<PraiseTypeResponse> myPraiseStamp,

        @Schema(description = "내가 받은 개선 피드백 목록")
        List<MyImprovementResponse> myImprovements,

        @Schema(description = "다른 멤버들이 받은 피드백 (멤버당 최신 1개)")
        List<MemberFeedbackPreviewResponse> memberFeedbacks
) {

    @Builder
    public record MyImprovementResponse(
            @Schema(description = "카테고리", example = "KITCHEN")
            TaskCategory category,

            @Schema(description = "피드백 내용", example = "뽀득한 그릇을 위해 뜨거운 물로 꼼꼼히 닦아줘!")
            String content,

            @Schema(description = "작성자 이름")
            String authorName,

            @Schema(description = "작성자 프로필 이미지 URL")
            String profileImageUrl
    ) {
    }

    @Builder
    public record MemberFeedbackPreviewResponse(
            @Schema(description = "멤버 ID")
            Long memberId,

            @Schema(description = "멤버 이름")
            String nickname,

            @Schema(description = "가장 최근에 받은 피드백 내용 (없으면 null)")
            String latestFeedbackContent
    ) {
    }
}
