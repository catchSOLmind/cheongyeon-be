package com.catchsolmind.cheongyeonbe.domain.group.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GroupMemberListResponse {

    private Long groupId;
    private Integer memberCount;
    private List<GroupMemberItemDto> members;

    @Getter
    @Builder
    public static class GroupMemberItemDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
        private MemberRole role;
        private MemberStatus status;
        private LocalDateTime joinedAt;
    }
}
