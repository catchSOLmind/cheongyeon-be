package com.catchsolmind.cheongyeonbe.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProfileUpdateResponse {

    private String nickname;
    private String email;
    private String profileImageUrl;
    private String houseworkType;
    private String houseworkTypeLabel;
    private LocalDateTime updatedAt;
}
