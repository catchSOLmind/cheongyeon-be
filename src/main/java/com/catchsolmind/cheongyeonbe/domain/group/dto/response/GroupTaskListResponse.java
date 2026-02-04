package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class GroupTaskListResponse {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<LocalDate> weekDates;
    private LocalDate selectedDate;
    private List<GroupTaskItemDto> items;

    @Getter
    @Builder
    public static class GroupTaskItemDto {
        private Long occurrenceId;
        private Long taskId;
        private Long taskTypeId;
        private String taskName;
        private TaskCategory category;
        private Integer point;
        private String time;
        private TaskStatus status;
        private boolean isTakeover;
        private AssigneeDto assignee;
    }

    @Getter
    @Builder
    public static class AssigneeDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
    }
}
