package com.catchsolmind.cheongyeonbe.domain.group.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    Optional<GroupInvitation> findByInvitationIdAndUsedAtIsNull(Long invitationId);
}
