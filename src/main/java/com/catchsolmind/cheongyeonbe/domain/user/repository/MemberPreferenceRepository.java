package com.catchsolmind.cheongyeonbe.domain.user.repository;

import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberPreference;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberPreferenceRepository
        extends JpaRepository<MemberPreference, Long> {

    Optional<MemberPreference> findByMember(GroupMember member);
}
