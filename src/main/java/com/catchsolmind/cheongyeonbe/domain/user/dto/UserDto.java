package com.catchsolmind.cheongyeonbe.domain.user.dto;

public record UserDto(
        Long userId,

        String name,

        String email,

        String nickname,

        String profileImg
) {
}
