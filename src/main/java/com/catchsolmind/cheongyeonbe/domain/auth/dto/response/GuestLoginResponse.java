package com.catchsolmind.cheongyeonbe.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record GuestLoginResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String nickname,
        Long groupId,
        String groupName,
        String memberStatus
) {}
