package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskLogFixture {

    private static TaskLog.TaskLogBuilder baseBuilder() {
        return TaskLog.builder()
                .taskLogId(1L)
                .doneAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .memo("memo");
    }

    public static TaskLog base() {
        return baseBuilder().build();
    }

    public static List<TaskLog> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .taskLogId((long) (i + 1))
                        .memo("memo-" + (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
