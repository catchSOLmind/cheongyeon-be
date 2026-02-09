package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyTaskCreateRequest {
    private List<TaskItemDto> tasks;

    @Getter
    @NoArgsConstructor
    public static class TaskItemDto {
        private LocalDate date;
        private Long taskTypeId;
        private String time;               // "11:00"
        private Long assigneeMemberId;
        private RepeatDto repeat;
    }

    @Getter
    @NoArgsConstructor
    public static class RepeatDto {
        private Boolean enabled;
        private List<String> daysOfWeek;  // ["MON", "WED"]
    }
}
