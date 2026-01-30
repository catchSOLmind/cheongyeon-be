package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyTaskStatusUpdateResponse {
    private Long occurrenceId;
    private TaskStatus status;

    private Long doneByMemberId;
    private LocalDateTime doneAt;

    private LocalDateTime updatedAt;
}
