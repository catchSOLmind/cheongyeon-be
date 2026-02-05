package com.catchsolmind.cheongyeonbe.domain.group.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    // 기존: 사용되지 않은 초대만 조회 (1회용)
    Optional<GroupInvitation> findByInvitationIdAndUsedAtIsNull(Long invitationId);

    // 신규: 초대 링크 재사용 가능 (시연용 - usedAt 체크 없음)
    Optional<GroupInvitation> findByInvitationId(Long invitationId);
}
