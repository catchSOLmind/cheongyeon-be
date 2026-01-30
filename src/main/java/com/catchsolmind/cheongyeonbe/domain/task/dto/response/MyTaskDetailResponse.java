package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MyTaskDetailResponse {
    private Long occurrenceId;
    private Long taskId;
    private Long taskTypeId;

    private TaskCategory category;
    private String taskName;
    private Integer point;

    private LocalDate date;
    private String time;       // 엔터티에 없으면 null

    private boolean repeatEnabled;
    private String repeatRule;

    private Long primaryAssignedMemberId;
    private TaskStatus status;
    private boolean isTakeover;
}
