package com.catchsolmind.cheongyeonbe.domain.group.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    boolean existsByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    Optional<GroupMember> findFirstByUser_UserIdOrderByGroupMemberIdDesc(Long userId);

    // LEFT 상태 제외 멤버 목록
    List<GroupMember> findByGroup_GroupIdAndStatusNot(Long groupId, MemberStatus status);

    // 특정 상태가 아닌 멤버 조회 (그룹 + 유저 + 상태)
    Optional<GroupMember> findByGroup_GroupIdAndUser_UserIdAndStatusNot(Long groupId, Long userId, MemberStatus status);

    // 그룹 전체 멤버 목록
    List<GroupMember> findByGroup_GroupId(Long groupId);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.userId = :userId AND gm.status = 'JOINED'")
    Optional<Group> findGroupByUserId(@Param("userId") Long userId);

    // 특정 상태인 내 정보 찾기 (피드백 작성 가능한지 보기 위해)
    @EntityGraph(attributePaths = {"group"})
    Optional<GroupMember> findByUser_UserIdAndStatus(Long userId, MemberStatus status);

    // 특정 상태인 그룹 멤버 목록 찾기
    @EntityGraph(attributePaths = {"user"})
    List<GroupMember> findByGroup_GroupIdAndStatus(Long groupId, MemberStatus status);

    // 특정 그룹의 활성 멤버 수 카운트
    long countByGroup_GroupIdAndStatus(Long groupId, MemberStatus status);
}
