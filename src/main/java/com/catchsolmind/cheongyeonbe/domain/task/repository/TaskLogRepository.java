package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    Optional<TaskLog> findTop1ByOccurrence_OccurrenceIdOrderByTaskLogIdDesc(Long occurrenceId);

    boolean existsByOccurrence_OccurrenceId(Long occurrenceId);

    void deleteByOccurrence_OccurrenceId(Long occurrenceId);
}
