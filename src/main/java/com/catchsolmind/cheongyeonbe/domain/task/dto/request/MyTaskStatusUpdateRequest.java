package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyTaskStatusUpdateRequest {
    private TaskStatus status;
}
