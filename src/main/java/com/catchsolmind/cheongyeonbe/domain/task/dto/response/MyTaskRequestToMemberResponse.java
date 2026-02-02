package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyTaskRequestToMemberResponse {
    private Long occurrenceId;
    private Long fromMemberId;
    private Long toMemberId;
    private Long updatedAssigneeMemberId;
    private LocalDateTime updatedAt;
}
