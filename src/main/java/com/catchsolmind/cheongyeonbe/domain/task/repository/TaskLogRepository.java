package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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
    Optional<LocalDateTime> findLastDoneDate(@Param("groupId") Long groupId,
                                             @Param("taskTypeId") Long taskTypeId);

    // 한 번에 여러 TaskType의 마지막 수행일 조회
    @Query("SELECT t.taskType.taskTypeId, MAX(tl.doneAt) " +
            "FROM TaskLog tl " +
            "JOIN tl.occurrence tlo " +
            "JOIN tlo.task t " +
            "WHERE tlo.group.groupId = :groupId " +
            "AND t.taskType.taskTypeId IN :taskTypeIds " +
            "GROUP BY t.taskType.taskTypeId")
    List<Object[]> findLastDoneDatesByGroupAndTaskTypes(@Param("groupId") Long groupId,
                                                        @Param("taskTypeIds") List<Long> taskTypeIds);

    @Query("SELECT tt.category, COUNT(tl) " +
            "FROM TaskLog tl " +
            "JOIN tl.occurrence occ " +
            "JOIN occ.task t " +
            "JOIN t.taskType tt " +
            "WHERE occ.group.groupId = :groupId " +
            "GROUP BY tt.category " +
            "ORDER BY COUNT(tl) DESC")
    List<Object[]> findCategoryRankStats(@Param("groupId") Long groupId, Pageable pageable);
}
