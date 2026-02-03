package com.catchsolmind.cheongyeonbe.domain.group.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    boolean existsByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);
}
