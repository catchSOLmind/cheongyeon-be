package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {

    // 카테고리별 조회
    List<TaskType> findByCategory(TaskCategory category);

    // taskTypeId 목록으로 조회
    List<TaskType> findByTaskTypeIdIn(List<Long> taskTypeIds);

    List<TaskType> findByCategoryAndSubCategory(TaskCategory category, TaskSubCategory subCategory);

}
