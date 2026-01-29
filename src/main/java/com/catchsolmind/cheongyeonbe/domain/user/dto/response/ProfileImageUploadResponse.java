package com.catchsolmind.cheongyeonbe.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileImageUploadResponse {
    private String profileImageUrl;
}
