package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
