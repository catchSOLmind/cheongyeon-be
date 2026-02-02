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
        List<String> representativeLines,
        String cautionPoint
) {
    public static HouseworkTestResultResponse from(TestResultType type) {
        return new HouseworkTestResultResponse(
                type,
                type.getTitle(),
                type.getSubTitle(),
                type.getMainQuote(),
                type.getTags(),
                type.getDescription(),
                type.getRepresentativeLines(),
                type.getCautionPoint()
        );
    }
}
