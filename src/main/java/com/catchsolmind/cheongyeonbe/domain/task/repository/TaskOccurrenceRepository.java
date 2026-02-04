package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskOccurrenceRepository extends JpaRepository<TaskOccurrence, Long> {

    List<TaskOccurrence> findByGroup_GroupIdAndPrimaryAssignedMember_GroupMemberIdAndOccurDate(
            Long groupId, Long memberId, LocalDate date
    );

    List<TaskOccurrence> findByGroup_GroupIdAndPrimaryAssignedMember_GroupMemberIdAndOccurDateBetween(
            Long groupId, Long memberId, LocalDate start, LocalDate end
    );

    Optional<TaskOccurrence> findByOccurrenceIdAndGroup_GroupId(Long occurrenceId, Long groupId);

    boolean existsByOccurrenceIdAndGroup_GroupId(Long occurrenceId, Long groupId);

    // 전체 할일: 그룹 전체 멤버의 특정 날짜 occurrence 조회
    List<TaskOccurrence> findByGroup_GroupIdAndOccurDate(Long groupId, LocalDate date);

    // 전체 할일: 그룹 전체 멤버의 특정 기간 occurrence 조회
    List<TaskOccurrence> findByGroup_GroupIdAndOccurDateBetween(Long groupId, LocalDate start, LocalDate end);

    // 대시보드: 특정 상태 occurrence 수
    long countByGroup_GroupIdAndOccurDateBetweenAndStatus(Long groupId, LocalDate start, LocalDate end, TaskStatus status);

    // 대시보드: 전체 occurrence 수
    long countByGroup_GroupIdAndOccurDateBetween(Long groupId, LocalDate start, LocalDate end);

    // 그룹의 완료되지 않은(WAITING) 작업들 조회 (Task와 TaskType까지 한 번에 로딩)
    @Query("SELECT t FROM TaskOccurrence t " +
            "JOIN FETCH t.task task " +
            "JOIN FETCH task.taskType " +
            "WHERE t.group.groupId = :groupId " +
            "AND t.status = 'WAITING'")
    List<TaskOccurrence> findUnfinishedByGroupId(@Param("groupId") Long groupId);
}
