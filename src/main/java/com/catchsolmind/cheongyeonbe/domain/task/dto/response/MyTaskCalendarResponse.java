package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MyTaskCalendarResponse {
    private int year;
    private int month;
    private List<LocalDate> taskDates;
}
