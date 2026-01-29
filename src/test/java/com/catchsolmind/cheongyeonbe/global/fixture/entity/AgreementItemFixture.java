package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgreementItemFixture {

    public static AgreementItem agreementItem() {
        return AgreementItem.builder()
                .itemId(1L)
                .agreement(AgreementFixture.agreement())
                .itemText("item-text-1")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static AgreementItem agreementItem2() {
        return AgreementItem.builder()
                .itemId(2L)
                .agreement(AgreementFixture.agreement())
                .itemText("item-text-2")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static List<AgreementItem> agreementItems() {
        List<AgreementItem> agreementItems = new ArrayList<>();
        agreementItems.add(AgreementItemFixture.agreementItem());
        agreementItems.add(AgreementItemFixture.agreementItem2());

        return agreementItems;
    }
}
