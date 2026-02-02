package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyTaskUpdateRequest {

    private Long taskTypeId;
    private String date;        // "2026-01-22"
    private String time;        // "10:30"
    private RepeatDto repeat;
    private Long assigneeMemberId;

    @Getter
    @NoArgsConstructor
    public static class RepeatDto {
        private Boolean enabled;
        private List<String> daysOfWeek;  // ["MON", "WED"]
    }
}