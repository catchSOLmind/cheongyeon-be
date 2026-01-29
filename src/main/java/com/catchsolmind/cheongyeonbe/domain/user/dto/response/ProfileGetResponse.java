package com.catchsolmind.cheongyeonbe.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileGetResponse {

    private Profile profile;

    @Getter
    @Builder
    public static class Profile {
        private String nickname;
        private String email;
        private String profileImageUrl;
        private String houseworkType;
        private String houseworkTypeLabel;
    }
}
