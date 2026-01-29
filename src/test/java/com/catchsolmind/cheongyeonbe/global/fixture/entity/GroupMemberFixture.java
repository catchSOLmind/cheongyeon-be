package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GroupMemberFixture {

    private static GroupMember.GroupMemberBuilder baseBuilder() {
        return GroupMember.builder()
                .groupMemberId(1L)
                .role(MemberRole.MEMBER)
                .status(MemberStatus.JOINED)
                .joinedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .agreedAt(LocalDateTime.of(2026, 1, 2, 12, 0, 0));
    }

    public static GroupMember base() {
        return baseBuilder().build();
    }

    public static List<GroupMember> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .groupMemberId((long) (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
