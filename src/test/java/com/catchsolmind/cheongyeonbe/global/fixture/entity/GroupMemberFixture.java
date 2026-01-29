package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberFixture {

    public static GroupMember groupMember() {
        return GroupMember.builder()
                .groupMemberId(1L)
                .group(GroupFixture.group())
                .user(UserFixture.user())
                .role(MemberRole.MEMBER)
                .status(MemberStatus.JOINED)
                .joinedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .agreedAt(LocalDateTime.of(2026, 1, 2, 12, 0, 0))
                .agreementSigns(AgreementSignFixture.agreementSigns())
                .assignedOccurrences(TaskOccurrenceFixture.taskOccurrences())
                .preference(MemberPreferenceFixture.memberPreference1())
                .build();
    }

    public static GroupMember groupMember2() {
        return GroupMember.builder()
                .groupMemberId(2L)
                .group(GroupFixture.group())
                .user(UserFixture.user())
                .role(MemberRole.OWNER)
                .status(MemberStatus.JOINED)
                .joinedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .agreedAt(LocalDateTime.of(2026, 1, 2, 12, 0, 0))
                .agreementSigns(AgreementSignFixture.agreementSigns())
                .assignedOccurrences(TaskOccurrenceFixture.taskOccurrences())
                .preference(MemberPreferenceFixture.memberPreference1())
                .build();
    }

    public static List<GroupMember> groupMembers() {
        List<GroupMember> groupMembers = new ArrayList<>();
        groupMembers.add(GroupMemberFixture.groupMember());
        groupMembers.add(GroupMemberFixture.groupMember2());

        return groupMembers;
    }
}
