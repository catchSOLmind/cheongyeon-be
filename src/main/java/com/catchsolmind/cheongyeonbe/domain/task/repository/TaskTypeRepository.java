package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {
    List<TaskType> findByCategory(TaskCategory category);
}
