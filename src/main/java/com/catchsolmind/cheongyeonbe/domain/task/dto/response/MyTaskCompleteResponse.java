package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyTaskCompleteResponse {
    private Long occurrenceId;
    private TaskStatus status;
    private Integer earnedPoint;
    private String completedAt;
}
