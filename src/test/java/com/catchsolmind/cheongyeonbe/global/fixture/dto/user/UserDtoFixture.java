package com.catchsolmind.cheongyeonbe.global.fixture.dto.user;

import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;

public class UserDtoFixture {

    public static UserDto valid() {
        return UserDto.builder()
                .userId(1L)
                .email("email@email.co")
                .nickname("nickname")
                .profileImg("profileImg")
                .build();
    }
}
