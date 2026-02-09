package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.point.entity.PointTransaction;
import com.catchsolmind.cheongyeonbe.global.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PointTransactionFixture {

    private static PointTransaction.PointTransactionBuilder baseBuilder() {
        return PointTransaction.builder()
                .transactionId(1L)
                .amount(1)
                .transactionType(TransactionType.USE_MAGIC_ERASER)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static PointTransaction base() {
        return baseBuilder().build();
    }

    public static List<PointTransaction> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .transactionId((long) (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
