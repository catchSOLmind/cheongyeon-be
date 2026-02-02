package com.catchsolmind.cheongyeonbe.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {
    private String nickname;
    private String houseworkType;
}
