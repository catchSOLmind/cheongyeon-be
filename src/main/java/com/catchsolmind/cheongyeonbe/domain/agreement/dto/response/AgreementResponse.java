package com.catchsolmind.cheongyeonbe.domain.agreement.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.AgreementStatus;
import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.SignStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AgreementResponse {

    private Long agreementId;
    private AgreementStatus status;
    private String deadline;
    private String houseName;
    private String monthlyGoal;
    private List<RuleDto> rules;
    private List<MemberSignDto> members;
    private LocalDateTime confirmedAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class RuleDto {
        private Long itemId;
        private Integer itemOrder;
        private String itemText;
    }

    @Getter
    @Builder
    public static class MemberSignDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
        private MemberRole role;
        private SignStatus signStatus;
        private LocalDateTime signedAt;
    }
}
