package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    Optional<TaskLog> findTop1ByOccurrence_OccurrenceIdOrderByTaskLogIdDesc(Long occurrenceId);

    boolean existsByOccurrence_OccurrenceId(Long occurrenceId);

    void deleteByOccurrence_OccurrenceId(Long occurrenceId);

    // 특정 그룹이 특정 업무(TaskType)를 가장 최근에 완료한 시간 조회
    @Query("SELECT MAX(tl.doneAt) FROM TaskLog tl " +
            "JOIN tl.occurrence tlo " +
            "JOIN tlo.task t " +
            "WHERE tlo.group.groupId = :groupId " +
            "AND t.taskType.taskTypeId = :taskTypeId")
    LocalDateTime findLastDoneDate(@Param("groupId") Long groupId,
                                   @Param("taskTypeId") Long taskTypeId);
}
