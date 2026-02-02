package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.PostponeReasonCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyTaskScheduleUpdateResponse {
    private Long occurrenceId;
    private String date;
    private String time;
    private PostponeReasonDto postponeReason;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class PostponeReasonDto {
        private PostponeReasonCode reasonCode;
        private String reasonText;
    }
}
