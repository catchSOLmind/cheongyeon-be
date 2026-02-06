package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskOccurrenceRepository extends JpaRepository<TaskOccurrence, Long> {

    List<TaskOccurrence> findByGroup_GroupIdAndPrimaryAssignedMember_GroupMemberIdAndOccurDate(
            Long groupId, Long memberId, LocalDate date
    );

    // 내 할일: memberId와 날짜로 조회 (groupId 없이)
    List<TaskOccurrence> findByPrimaryAssignedMember_GroupMemberIdAndOccurDate(
            Long memberId, LocalDate date
    );

    List<TaskOccurrence> findByGroup_GroupIdAndPrimaryAssignedMember_GroupMemberIdAndOccurDateBetween(
            Long groupId, Long memberId, LocalDate start, LocalDate end
    );

    Optional<TaskOccurrence> findByOccurrenceIdAndGroup_GroupId(Long occurrenceId, Long groupId);

    // 내 할일: occurrenceId와 memberId로 조회 (groupId 없이, 본인 검증 포함)
    Optional<TaskOccurrence> findByOccurrenceIdAndPrimaryAssignedMember_GroupMemberId(Long occurrenceId, Long memberId);

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

    // 특정 그룹의 특정 집안일 중, 아직 안 끝난 것 찾기
    @Query("SELECT to FROM TaskOccurrence to " +
            "JOIN FETCH to.task t " +
            "JOIN FETCH t.taskType " +
            "WHERE t.group.groupId = :groupId " +
            "AND t.taskType.taskTypeId = :taskTypeId " +
            "AND to.status IN :statuses")
    List<TaskOccurrence> findByGroupAndTaskTypeAndStatusIn(
            @Param("groupId") Long groupId,
            @Param("taskTypeId") Long taskTypeId,
            @Param("statuses") List<TaskStatus> statuses
    );

    // 일괄 업데이트 쿼리 (성능 최적화)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TaskOccurrence to SET to.status = :newStatus " +
            "WHERE to.task.taskType.taskTypeId = :taskTypeId " +
            "AND to.task.group.groupId = :groupId " +
            "AND to.status IN :oldStatuses")
    void bulkUpdateStatus(
            @Param("groupId") Long groupId,
            @Param("taskTypeId") Long taskTypeId,
            @Param("oldStatuses") List<TaskStatus> oldStatuses,
            @Param("newStatus") TaskStatus newStatus
    );
}
