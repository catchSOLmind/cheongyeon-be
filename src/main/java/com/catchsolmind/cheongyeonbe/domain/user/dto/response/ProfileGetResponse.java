package com.catchsolmind.cheongyeonbe.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileGetResponse {

    private Profile profile;
    private Summary summary;
    private MonthlyActivity monthlyActivity;

    @Getter
    @Builder
    public static class Profile {
        private Long userId;
        private String nickname;
        private String email;
        private String profileImageUrl;
        private String houseworkType;
        private String houseworkTypeLabel;
    }

    @Getter
    @Builder
    public static class Summary {
        private Integer streakDays;
        private Integer totalPoints;
        private Integer completedTaskCount;
    }

    @Getter
    @Builder
    public static class MonthlyActivity {
        private String month;           // "2026-01"
        private Integer totalCount;
        private List<CategoryActivity> categories;
    }

    @Getter
    @Builder
    public static class CategoryActivity {
        private String categoryName;
        private Integer count;
        private Integer mySharePercent;
    }
}
