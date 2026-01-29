package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskOccurrenceFixture {

    private static TaskOccurrence.TaskOccurrenceBuilder baseBuilder() {
        return TaskOccurrence.builder()
                .occurrenceId(1L)
                .occurDate(LocalDate.of(2026, 1, 1))
                .status(TaskStatus.UNCOMPLETED)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static TaskOccurrence base() {
        return baseBuilder().build();
    }

    public static List<TaskOccurrence> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .occurrenceId((long) (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
