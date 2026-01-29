package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberPreference;

import java.time.LocalDateTime;

public class MemberPreferenceFixture {

    public static MemberPreference memberPreference1() {
        return MemberPreference.builder()
                .preferenceId(1L)
                .member(GroupMemberFixture.groupMember())
                .preferredTimeSlots("[\"morning\", \"evening\"]")
                .personalityType("personality-type")
                .testResult("test-result")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }
}
