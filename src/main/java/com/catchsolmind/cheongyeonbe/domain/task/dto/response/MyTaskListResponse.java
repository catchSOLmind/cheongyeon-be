package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.ManagerCallResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.data.MyTaskItemDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MyTaskListResponse {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<LocalDate> weekDates;

    private LocalDate selectedDate;
    private List<MyTaskItemDto> items;

    private List<ManagerCallResponse> managerCall;
}
