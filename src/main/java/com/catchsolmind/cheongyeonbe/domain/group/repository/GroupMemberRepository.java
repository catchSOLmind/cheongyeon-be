package com.catchsolmind.cheongyeonbe.domain.group.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    boolean existsByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.userId = :userId AND gm.status = 'JOINED'")
    Optional<Group> findGroupByUserId(@Param("userId") Long userId);
}
