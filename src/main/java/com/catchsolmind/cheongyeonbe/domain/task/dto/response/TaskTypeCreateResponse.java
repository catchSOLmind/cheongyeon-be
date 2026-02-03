package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskTypeCreateResponse {
    private Long taskTypeId;
    private TaskCategory category;
    private TaskSubCategory subCategory;
    private String name;
}
