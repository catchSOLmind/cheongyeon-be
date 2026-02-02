package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskFixture {

    private static Task.TaskBuilder baseBuilder() {
        return Task.builder()
                .taskId(1L)
                .title("title")
                .description("description")
                .repeatRule("repeat-rule")
                .status(TaskStatus.COMPLETED)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static Task base() {
        return baseBuilder().build();
    }

    public static List<Task> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .taskId((long) (i + 1))
                        .title("title-" + (i + 1))
                        .description("description-" + (i + 1))
                        .repeatRule("repeat-rule-" + (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
