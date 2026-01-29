package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;

import java.time.LocalDateTime;

public class GroupFixture {

    public static Group group() {
        return Group.builder()
                .groupId(1L)
                .name("group name1")
                .ownerUser(UserFixture.user2())
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .members(GroupMemberFixture.groupMembers())
                .tasks(TaskFixture.tasks())
                .build();
    }
}
