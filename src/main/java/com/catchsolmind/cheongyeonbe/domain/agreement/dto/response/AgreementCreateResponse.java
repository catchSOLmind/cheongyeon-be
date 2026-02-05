package com.catchsolmind.cheongyeonbe.domain.agreement.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.AgreementStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AgreementCreateResponse {

    private Long agreementId;
    private AgreementStatus status;
    private String deadline;
    private String houseName;
    private String monthlyGoal;
    private List<RuleDto> rules;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class RuleDto {
        private Long itemId;
        private Integer itemOrder;
        private String itemText;
    }
}
