package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskTakeover;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskTakeoverFixture {

    private static TaskTakeover.TaskTakeoverBuilder baseBuilder() {
        return TaskTakeover.builder()
                .takeoverId(1L)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static TaskTakeover base() {
        return baseBuilder().build();
    }

    public static List<TaskTakeover> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .takeoverId((long) (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
