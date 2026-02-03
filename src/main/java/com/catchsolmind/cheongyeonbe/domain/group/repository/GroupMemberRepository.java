package com.catchsolmind.cheongyeonbe.domain.group.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    boolean existsByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    // LEFT 상태 제외 멤버 목록
    List<GroupMember> findByGroup_GroupIdAndStatusNot(Long groupId, MemberStatus status);

    // 그룹 전체 멤버 목록
    List<GroupMember> findByGroup_GroupId(Long groupId);
}
