package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskTakeover;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskTakeoverFixture {
    public static TaskTakeover taskTakeover() {
        return TaskTakeover.builder()
                .takeoverId(1L)
                .occurrence(TaskOccurrenceFixture.taskOccurrence())
                .fromMember(GroupMemberFixture.groupMember())
                .toMember(GroupMemberFixture.groupMember2())
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static TaskTakeover taskTakeover2() {
        return TaskTakeover.builder()
                .takeoverId(2L)
                .occurrence(TaskOccurrenceFixture.taskOccurrence())
                .fromMember(GroupMemberFixture.groupMember())
                .toMember(GroupMemberFixture.groupMember2())
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static List<TaskTakeover> takeovers() {
        List<TaskTakeover> takeovers = new ArrayList<>();
        takeovers.add(TaskTakeoverFixture.taskTakeover());
        takeovers.add(TaskTakeoverFixture.taskTakeover2());

        return takeovers;
    }
}
