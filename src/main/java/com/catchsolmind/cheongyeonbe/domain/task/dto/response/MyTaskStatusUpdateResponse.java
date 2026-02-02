package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.IncompleteReasonCode;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyTaskStatusUpdateResponse {
    private Long occurrenceId;
    private TaskStatus status;
    private IncompleteReasonDto incompleteReason;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class IncompleteReasonDto {
        private IncompleteReasonCode reasonCode;
        private String reasonText;
    }
}
