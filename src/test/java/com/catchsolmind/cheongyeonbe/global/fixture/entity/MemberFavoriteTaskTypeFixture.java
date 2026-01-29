package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberFavoriteTaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MemberFavoriteTaskTypeFixture {

    public static MemberFavoriteTaskType memberFavoriteTaskType() {
        return MemberFavoriteTaskType.builder()
                .id(1L)
                .member(GroupMemberFixture.groupMember())
                .taskType(TaskTypeFixture.taskType())
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static MemberFavoriteTaskType memberFavoriteTaskType2() {
        return MemberFavoriteTaskType.builder()
                .id(2L)
                .member(GroupMemberFixture.groupMember())
                .taskType(TaskTypeFixture.taskType())
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static List<MemberFavoriteTaskType> memberFavoriteTaskTypes() {
        List<MemberFavoriteTaskType> memberFavoriteTaskTypes = new ArrayList<>();
        memberFavoriteTaskTypes.add(MemberFavoriteTaskTypeFixture.memberFavoriteTaskType());
        memberFavoriteTaskTypes.add(MemberFavoriteTaskTypeFixture.memberFavoriteTaskType2());

        return memberFavoriteTaskTypes;
    }
}
