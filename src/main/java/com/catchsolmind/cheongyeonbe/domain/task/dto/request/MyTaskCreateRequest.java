package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyTaskCreateRequest {
    private LocalDate date;
    private List<Long> taskTypeIds;
    private String time;               // "11:00"
    private Long assigneeMemberId;
    private RepeatDto repeat;

    @Getter
    @NoArgsConstructor
    public static class RepeatDto {
        private Boolean enabled;
        private List<String> daysOfWeek;  // ["MON", "WED"]
    }
}
