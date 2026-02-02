package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.IncompleteReasonCode;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyTaskStatusUpdateRequest {
    private TaskStatus status;
    private IncompleteReasonCode reasonCode;    // INCOMPLETED일 때만 사용
    private String reasonText;                   // reasonCode=ETC일 때 필수
}
