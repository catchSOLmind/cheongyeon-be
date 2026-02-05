package com.catchsolmind.cheongyeonbe.domain.agreement.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.AgreementStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AgreementConfirmResponse {

    private Long agreementId;
    private AgreementStatus status;
    private LocalDateTime confirmedAt;
    private String houseName;
}
