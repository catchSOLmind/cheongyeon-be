package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskIncompleteLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskIncompleteLogRepository extends JpaRepository<TaskIncompleteLog, Long> {

    // 특정 멤버의 미완료 횟수
    long countByMember_GroupMemberId(Long memberId);

    // 특정 occurrence의 미완료 기록
    List<TaskIncompleteLog> findByOccurrence_OccurrenceId(Long occurrenceId);

    // 특정 멤버의 미완료 기록 목록
    List<TaskIncompleteLog> findByMember_GroupMemberId(Long memberId);
}
