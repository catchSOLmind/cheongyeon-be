package com.catchsolmind.cheongyeonbe.domain.group.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberFavoriteTaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberFavoriteTaskTypeRepository extends JpaRepository<MemberFavoriteTaskType, Long> {

    // 특정 멤버의 즐겨찾기 목록
    List<MemberFavoriteTaskType> findByMember_GroupMemberId(Long memberId);

    // 특정 멤버가 특정 taskType을 즐겨찾기 했는지 확인
    boolean existsByMember_GroupMemberIdAndTaskType_TaskTypeId(Long memberId, Long taskTypeId);

    // 특정 멤버의 특정 taskType 즐겨찾기 조회
    Optional<MemberFavoriteTaskType> findByMember_GroupMemberIdAndTaskType_TaskTypeId(Long memberId, Long taskTypeId);

    // 특정 멤버의 즐겨찾기한 taskTypeId 목록
    List<MemberFavoriteTaskType> findAllByMember_GroupMemberId(Long memberId);
}
