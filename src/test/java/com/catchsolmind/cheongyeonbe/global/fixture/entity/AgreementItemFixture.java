package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AgreementItemFixture {

    private static AgreementItem.AgreementItemBuilder baseBuilder() {
        return AgreementItem.builder()
                .itemId(1L)
                .itemText("item-text")
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static AgreementItem base() {
        return baseBuilder().build();
    }

    public static List<AgreementItem> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .itemId((long) (i + 1))
                        .itemText("item-text-" + (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
