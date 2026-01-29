package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.point.PointTransaction;
import com.catchsolmind.cheongyeonbe.global.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PointTransactionFixture {

    public static PointTransaction transaction() {
        return PointTransaction.builder()
                .transactionId(1L)
                .user(UserFixture.user())
                .amount(1)
                .transactionType(TransactionType.USE_MAGIC_ERASER)
                .taskLog(TaskLogFixture.taskLog())
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static PointTransaction transaction2() {
        return PointTransaction.builder()
                .transactionId(2L)
                .user(UserFixture.user())
                .amount(1)
                .transactionType(TransactionType.USE_MAGIC_ERASER)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0))
                .build();
    }

    public static List<PointTransaction> transactions() {
        List<PointTransaction> transactions = new ArrayList<>();
        transactions.add(PointTransactionFixture.transaction());
        transactions.add(PointTransactionFixture.transaction2());

        return transactions;
    }
}
