package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementSign;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgreementSignFixture {

    public static AgreementSign agreementSign() {
        return AgreementSign.builder()
                .signId(1L)
                .agreement(AgreementFixture.agreement())
                .member(GroupMemberFixture.groupMember())
                .signedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static AgreementSign agreementSign2() {
        return AgreementSign.builder()
                .signId(2L)
                .agreement(AgreementFixture.agreement())
                .member(GroupMemberFixture.groupMember())
                .signedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static List<AgreementSign> agreementSigns() {
        List<AgreementSign> agreementSigns = new ArrayList<>();
        agreementSigns.add(AgreementSignFixture.agreementSign());
        agreementSigns.add(AgreementSignFixture.agreementSign2());

        return agreementSigns;
    }
}
