package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {

    // 카테고리별 조회
    List<TaskType> findByCategory(TaskCategory category);

    // 이름 검색 (부분 일치)
    List<TaskType> findByNameContaining(String name);

    // 카테고리 + 이름 검색
    List<TaskType> findByCategoryAndNameContaining(TaskCategory category, String name);

    // taskTypeId 목록으로 조회
    List<TaskType> findByTaskTypeIdIn(List<Long> taskTypeIds);
}
