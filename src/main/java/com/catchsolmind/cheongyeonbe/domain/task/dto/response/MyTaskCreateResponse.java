package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
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
        private TaskCategory category;

        private String taskName;
        private Integer point;
    }
}
