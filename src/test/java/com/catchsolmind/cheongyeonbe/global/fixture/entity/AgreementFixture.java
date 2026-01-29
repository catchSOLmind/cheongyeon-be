package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.Agreement;

import java.time.LocalDateTime;

public class AgreementFixture {

    private static Agreement.AgreementBuilder baseBuilder() {
        return Agreement.builder()
                .agreementId(1L)
                .title("title")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .deletedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static Agreement base() {
        return baseBuilder().build();
    }
}
