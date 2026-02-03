package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskTypeCreateRequest {
    private TaskCategory category;
    private TaskSubCategory subCategory;  // ETC일 때만 사용 (선택)
    private String name;
}
