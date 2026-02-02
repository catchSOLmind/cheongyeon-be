package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskPostponeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskPostponeLogRepository extends JpaRepository<TaskPostponeLog, Long> {

    // 특정 멤버의 미룸 횟수
    long countByMember_GroupMemberId(Long memberId);

    // 특정 occurrence의 미룸 기록
    List<TaskPostponeLog> findByOccurrence_OccurrenceId(Long occurrenceId);

    // 특정 멤버의 미룸 기록 목록
    List<TaskPostponeLog> findByMember_GroupMemberId(Long memberId);
}
