package com.catchsolmind.cheongyeonbe.domain.agreement.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.SignStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AgreementSignResponse {

    private Long agreementId;
    private Long memberId;
    private SignStatus signStatus;
    private LocalDateTime signedAt;
    private Boolean allSigned;
    private Integer signedCount;
    private Integer totalCount;
}
