package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyTaskDeleteResponse {
    private Long occurrenceId;
    private LocalDateTime deletedAt;
}
