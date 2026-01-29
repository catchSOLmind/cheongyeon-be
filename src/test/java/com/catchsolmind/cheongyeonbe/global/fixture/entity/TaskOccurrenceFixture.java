package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskOccurrenceFixture {

    public static TaskOccurrence taskOccurrence() {
        return TaskOccurrence.builder()
                .occurrenceId(1L)
                .task(TaskFixture.task())
                .group(GroupFixture.group())
                .occurDate(LocalDate.of(2026, 1, 1))
                .primaryAssignedMember(GroupMemberFixture.groupMember())
                .status(TaskStatus.UNCOMPLETED)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .logs(TaskLogFixture.taskLogs())
                .takeovers(TaskTakeoverFixture.takeovers())
                .build();
    }

    public static TaskOccurrence taskOccurrence2() {
        return TaskOccurrence.builder()
                .occurrenceId(2L)
                .task(TaskFixture.task())
                .group(GroupFixture.group())
                .occurDate(LocalDate.of(2026, 1, 1))
                .primaryAssignedMember(GroupMemberFixture.groupMember())
                .status(TaskStatus.COMPLETED)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .logs(TaskLogFixture.taskLogs())
                .takeovers(TaskTakeoverFixture.takeovers())
                .build();
    }

    public static List<TaskOccurrence> taskOccurrences() {
        List<TaskOccurrence> taskOccurrences = new ArrayList<>();
        taskOccurrences.add(TaskOccurrenceFixture.taskOccurrence());
        taskOccurrences.add(TaskOccurrenceFixture.taskOccurrence2());

        return taskOccurrences;
    }
}
