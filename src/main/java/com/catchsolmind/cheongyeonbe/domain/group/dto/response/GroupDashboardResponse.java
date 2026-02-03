package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupDashboardResponse {

    // 이번 달 연속 청소 일수
    private Integer thisMonthStreakDays;

    // 이번 주 청소왕
    private CleaningKingDto thisWeekCleaningKing;

    // 집안일 완료율 (%)
    private Double houseworkCompletionRate;

    // 미루기 TOP 3
    private List<PostponeRankDto> postponeTop3;

    @Getter
    @Builder
    public static class CleaningKingDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
        private Integer completedCount;
    }

    @Getter
    @Builder
    public static class PostponeRankDto {
        private Integer rank;
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
        private Long postponeCount;
    }
}
