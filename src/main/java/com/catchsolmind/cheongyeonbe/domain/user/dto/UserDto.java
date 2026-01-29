package com.catchsolmind.cheongyeonbe.domain.user.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long userId,

        String email,

        String nickname,

        String profileImg
) {
}
