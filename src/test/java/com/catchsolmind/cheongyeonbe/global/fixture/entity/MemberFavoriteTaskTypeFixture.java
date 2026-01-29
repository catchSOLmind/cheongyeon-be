package com.catchsolmind.cheongyeonbe.global.fixture.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberFavoriteTaskType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemberFavoriteTaskTypeFixture {

    private static MemberFavoriteTaskType.MemberFavoriteTaskTypeBuilder baseBuilder() {
        return MemberFavoriteTaskType.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    }

    public static MemberFavoriteTaskType base() {
        return baseBuilder().build();
    }

    public static List<MemberFavoriteTaskType> createList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> baseBuilder()
                        .id((long) (i + 1))
                        .build())
                .collect(Collectors.toList());
    }
}
