package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupInvitationAcceptResponse {
    private Long groupId;
    private Long memberId;
    private MemberRole role;
    private MemberStatus status;
    private LocalDateTime joinedAt;
}
