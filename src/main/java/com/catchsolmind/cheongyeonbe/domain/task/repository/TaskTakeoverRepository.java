package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskTakeover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTakeoverRepository extends JpaRepository<TaskTakeover, Long> {
    boolean existsByOccurrence_OccurrenceId(Long occurrenceId);
}
