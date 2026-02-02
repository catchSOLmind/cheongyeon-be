package com.catchsolmind.cheongyeonbe.domain.group.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public Long getMemberId(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository
                .findByGroup_GroupIdAndUser_UserId(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User " + userId + " is not a member of group " + groupId
                ));

        return member.getGroupMemberId();
    }

    public boolean isMember(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroup_GroupIdAndUser_UserId(groupId, userId);
    }
}
