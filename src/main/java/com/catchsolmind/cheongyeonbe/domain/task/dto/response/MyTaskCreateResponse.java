package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTaskCreateResponse {
    private int createdCount;
    private List<CreatedMyTaskDto> created;

    @Getter
    @Builder
    public static class CreatedMyTaskDto {
        private Long taskId;
        private Long occurrenceId;
        private Long taskTypeId;

        private String taskName;
        private Integer point;
    }
}
