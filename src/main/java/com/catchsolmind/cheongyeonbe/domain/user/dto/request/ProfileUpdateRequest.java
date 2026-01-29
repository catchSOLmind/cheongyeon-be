package com.catchsolmind.cheongyeonbe.domain.user.dto.request;

import lombok.Getter;

@Getter
public class ProfileUpdateRequest {
    private String nickname;
    private String email;
    private String houseworkType;
}
