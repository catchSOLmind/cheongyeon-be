package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TaskTypeListResponse {
    private List<TaskTypeItemDto> items;               // 기존 업무

    private List<TaskTypeItemDto> newYearItems;         // 새해맞이 추천 업무
    private List<TaskTypeItemDto> personalityItems;     // 사용자 성향별 추천 업무
    private String personalityLabel;                    // 사용자 성향 이름 (뽀득이, 내일이 등)

    @Getter
    @Builder
    public static class TaskTypeItemDto {
        private Long taskTypeId;
        private TaskCategory category;
        private TaskSubCategory subCategory;  // ETC일 때 PET/BABY, 나머지는 null
        private String name;
        private Integer point;
        private Boolean isFavorite;
    }
}
