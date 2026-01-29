package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberPreference;

import java.time.LocalDateTime;

public class MemberPreferenceFixture {

    private static MemberPreference.MemberPreferenceBuilder baseBuilder() {
        return MemberPreference.builder()
                .preferenceId(1L)
                .preferredTimeSlots("preferred-time-slots")
                .personalityType("personality-type")
                .testResult("test-result")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static MemberPreference base() {
        return baseBuilder().build();
    }
}
