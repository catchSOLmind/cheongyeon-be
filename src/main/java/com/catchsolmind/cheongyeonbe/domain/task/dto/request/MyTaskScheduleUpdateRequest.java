package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.PostponeReasonCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyTaskScheduleUpdateRequest {
    private String date;                              // "2026-01-21"
    private String time;                              // "11:00"
    private PostponeReasonCode postponeReasonCode;    // 선택
    private String postponeReasonText;                // postponeReasonCode=ETC일 때 필수
}
