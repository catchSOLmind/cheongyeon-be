package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.Agreement;

import java.time.LocalDateTime;

public class AgreementFixture {

    public static Agreement agreement() {
        return Agreement.builder()
                .agreementId(1L)
                .group(GroupFixture.group())
                .title("title")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .deletedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .items(AgreementItemFixture.agreementItems())
                .signs(AgreementSignFixture.agreementSigns())
                .build();
    }
}
