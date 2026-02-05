package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupCreateResponse {
    private Long groupId;
    private MemberRole role;
    private LocalDateTime createdAt;
}
