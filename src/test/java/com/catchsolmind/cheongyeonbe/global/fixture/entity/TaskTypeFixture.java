package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;

import java.time.LocalDateTime;

public class TaskTypeFixture {

    private static TaskType.TaskTypeBuilder baseBuilder() {
        return TaskType.builder()
                .taskTypeId(1L)
                .category(TaskCategory.BATHROOM)
                .name("name")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static TaskType base() {
        return baseBuilder().build();
    }
}
