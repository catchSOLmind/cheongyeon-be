package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskTakeover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTakeoverRepository extends JpaRepository<TaskTakeover, Long> {

    boolean existsByOccurrence_OccurrenceId(Long occurrenceId);

    // 특정 멤버가 부탁한 횟수 (fromMember 기준)
    long countByFromMember_GroupMemberId(Long memberId);

    // 특정 멤버가 부탁받은 횟수 (toMember 기준)
    long countByToMember_GroupMemberId(Long memberId);
}
