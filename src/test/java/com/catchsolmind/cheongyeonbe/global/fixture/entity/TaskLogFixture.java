package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskLogFixture  {

    public static TaskLog taskLog() {
        return TaskLog.builder()
                .taskLogId(1L)
                .occurrence(TaskOccurrenceFixture.taskOccurrence())
                .doneByMember(GroupMemberFixture.groupMember())
                .doneAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .memo("memo")
                .pointTransactions(PointTransactionFixture.transactions())
                .build();
    }

    public static TaskLog taskLog2() {
        return TaskLog.builder()
                .taskLogId(2L)
                .occurrence(TaskOccurrenceFixture.taskOccurrence())
                .doneByMember(GroupMemberFixture.groupMember2())
                .doneAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .memo("memo")
                .pointTransactions(PointTransactionFixture.transactions())
                .build();
    }

    public static List<TaskLog> taskLogs() {
        List<TaskLog> taskLogs = new ArrayList<>();
        taskLogs.add(TaskLogFixture.taskLog());
        taskLogs.add(TaskLogFixture.taskLog2());

        return taskLogs;
    }
}
