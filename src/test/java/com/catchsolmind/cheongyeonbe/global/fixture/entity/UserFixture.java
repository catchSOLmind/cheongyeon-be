package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;

import java.time.LocalDateTime;

public class UserFixture {

    private static User.UserBuilder baseBuilder() {
        return User.builder()
                .userId(1L)
                .provider(AuthProvider.KAKAO)
                .providerId(123456789L)
                .email("email@kakao.com")
                .nickname("nickname")
                .profileImg("profile-img-url")
                .pointBalance(0)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static User base() {
        return baseBuilder().build();
    }
}
