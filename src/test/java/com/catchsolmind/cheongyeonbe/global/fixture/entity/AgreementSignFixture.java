package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementSign;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AgreementSignFixture {

    private static AgreementSign.AgreementSignBuilder baseBuilder() {
        return AgreementSign.builder()
                .signId(1L)
                .signedAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static AgreementSign base() {
        return baseBuilder().build();
    }

    public static List<AgreementSign> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .signId((long) (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
