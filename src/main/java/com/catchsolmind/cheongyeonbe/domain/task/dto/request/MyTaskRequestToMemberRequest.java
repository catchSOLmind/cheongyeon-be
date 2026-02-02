package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyTaskRequestToMemberRequest {
    private Long toMemberId;
    private String message;     // 선택
}
