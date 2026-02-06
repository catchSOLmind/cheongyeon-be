package com.catchsolmind.cheongyeonbe.domain.task.dto.data;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyTaskItemDto {
    private Long occurrenceId;
    private Long taskId;
    private Long taskTypeId;
    private TaskCategory category;

    private String taskName;
    private Integer point;

    private String time;
    private TaskStatus status;        // WAITING, IN_PROGRESS, INCOMPLETED, COMPLETED
    private boolean isTakeover;

    private Long primaryAssignedMemberId;
}
