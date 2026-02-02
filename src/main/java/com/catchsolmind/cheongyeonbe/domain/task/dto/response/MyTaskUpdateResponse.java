package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyTaskUpdateResponse {

    private Long occurrenceId;
    private Long taskId;
    private TaskTypeDto taskType;
    private String date;
    private String time;
    private RepeatDto repeat;
    private AssigneeDto assignee;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class TaskTypeDto {
        private Long taskTypeId;
        private TaskCategory category;
        private String name;
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