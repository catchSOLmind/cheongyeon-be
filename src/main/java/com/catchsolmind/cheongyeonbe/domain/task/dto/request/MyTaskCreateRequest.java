package com.catchsolmind.cheongyeonbe.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyTaskCreateRequest {
    private Long groupId;
    private LocalDate date;
    private List<Long> taskTypeIds;
}
