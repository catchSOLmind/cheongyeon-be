package com.catchsolmind.cheongyeonbe.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuggestionType {
    DELAYED(
            "미루어진 작업"
    ),
    NOASSIGNEE(
            "무담당 작업"
    ),
    GENERAL(
            "시즌 추천"
    ),
    REPEAT(
            "반복 작업"
    );

    private final String title;
}
