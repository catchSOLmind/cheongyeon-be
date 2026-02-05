package com.catchsolmind.cheongyeonbe.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileGetResponse {

    private Profile profile;
    private PersonalityInfo personalityInfo;
    private Summary summary;
    private MonthlyActivity monthlyActivity;

    @Getter
    @Builder
    public static class Profile {
        private Long userId;
        private Long groupId;
        private String nickname;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    public static class PersonalityInfo {
        private Boolean hasCompleted;       // 성향 테스트 완료 여부
        private String houseworkType;       // 성향 코드 (예: "EFFICIENCY")
        private String houseworkTypeLabel;  // 성향 라벨 (예: "효율이")
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
