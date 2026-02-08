package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;

import java.util.List;

public record HouseworkTestResultResponse(
        TestResultType resultType,
        String title,
        String subTitle,
        String mainQuote,
        List<String> tags,
        String description,
        List<Integer> scores,
        String cautionPoint
) {
    public static HouseworkTestResultResponse of(TestResultType type, List<Integer> calculatedScores) {
        return new HouseworkTestResultResponse(
                type,
                type.getTitle(),
                type.getSubTitle(),
                type.getMainQuote(),
                type.getTags(),
                type.getDescription(),
                calculatedScores,
                type.getCautionPoint()
        );
    }
}
