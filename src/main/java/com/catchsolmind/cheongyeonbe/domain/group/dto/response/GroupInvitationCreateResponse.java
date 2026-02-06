package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupInvitationCreateResponse {
    private Long invitationId;
    private String inviteUrl;
}
