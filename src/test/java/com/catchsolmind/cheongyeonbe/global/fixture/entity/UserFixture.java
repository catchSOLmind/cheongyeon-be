package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;

import java.time.LocalDateTime;

public class UserFixture {

    public static User user() {
        return User.builder()
                .userId(1L)
                .provider("KAKAO")
                .providerId("provider-id")
                .email("유저1@email.com")
                .nickname("유저1-닉네임")
                .profileImg("profile-img-url")
                .pointBalance(0)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .groupMembers(GroupMemberFixture.groupMembers())
                .pointTransactions(PointTransactionFixture.transactions())
                .build();
    }

    public static User user2() {
        return User.builder()
                .userId(2L)
                .provider("KAKAO")
                .providerId("provider-id")
                .email("유저2@email.com")
                .nickname("유저2-닉네임")
                .profileImg("profile-img-url")
                .pointBalance(0)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .groupMembers(GroupMemberFixture.groupMembers())
                .pointTransactions(PointTransactionFixture.transactions())
                .build();
    }
}
