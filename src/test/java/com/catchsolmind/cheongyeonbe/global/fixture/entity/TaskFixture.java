package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskFixture {

    public static Task task() {
        return Task.builder()
                .taskId(1L)
                .group(GroupFixture.group())
                .taskType(TaskTypeFixture.taskType())
                .title("title")
                .description("description")
                .creatorMember(GroupMemberFixture.groupMember())
                .repeatRule("repeat-rule")
                .status(TaskStatus.UNCOMPLETED)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .occurrences(TaskOccurrenceFixture.taskOccurrences())
                .build();
    }

    public static Task task2() {
        return Task.builder()
                .taskId(2L)
                .group(GroupFixture.group())
                .taskType(TaskTypeFixture.taskType())
                .title("title-2")
                .description("description-2")
                .creatorMember(GroupMemberFixture.groupMember())
                .repeatRule("repeat-rule-2")
                .status(TaskStatus.UNCOMPLETED)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .occurrences(TaskOccurrenceFixture.taskOccurrences())
                .build();
    }

    public static List<Task> tasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(TaskFixture.task());
        tasks.add(TaskFixture.task2());

        return tasks;
    }
}
