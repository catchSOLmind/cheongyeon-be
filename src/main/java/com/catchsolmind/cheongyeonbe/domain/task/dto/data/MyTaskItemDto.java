package com.catchsolmind.cheongyeonbe.domain.task.dto.data;

import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyTaskItemDto {
    private Long occurrenceId;
    private Long taskId;
    private Long taskTypeId;

    private String taskName;
    private Integer point;

    private String time;
    private TaskStatus status;        // UNCOMPLETED/COMPLETED
    private boolean isTakeover;

    private Long primaryAssignedMemberId;
}
