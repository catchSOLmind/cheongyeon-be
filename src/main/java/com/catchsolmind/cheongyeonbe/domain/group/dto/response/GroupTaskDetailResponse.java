package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupTaskDetailResponse {

    private Long occurrenceId;
    private Long taskId;
    private Long groupId;

    private TaskTypeDto taskType;
    private String date;
    private String time;
    private RepeatDto repeat;
    private AssigneeDto assignee;
    private TaskStatus status;
    private Boolean isTakeover;

    @Getter
    @Builder
    public static class TaskTypeDto {
        private Long taskTypeId;
        private TaskCategory category;
        private String name;
        private Integer point;
    }

    @Getter
    @Builder
    public static class RepeatDto {
        private Boolean enabled;
        private List<String> daysOfWeek;
    }

    @Getter
    @Builder
    public static class AssigneeDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
    }
}
