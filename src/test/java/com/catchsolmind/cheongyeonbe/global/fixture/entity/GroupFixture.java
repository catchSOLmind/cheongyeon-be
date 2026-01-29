package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;

import java.time.LocalDateTime;

public class GroupFixture {

    private static Group.GroupBuilder baseBuilder() {
        return Group.builder()
                .groupId(1L)
                .name("name")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static Group base() {
        return baseBuilder().build();
    }
}
