package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupDashboardResponse {

    private Integer thisMonthStreakDays;
    private CleaningKingDto thisWeekCleaningKing;
    private Double houseworkCompletionRate;
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
