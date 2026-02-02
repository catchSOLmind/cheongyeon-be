package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;

public record HouseworkTestAnswerRequest(
        Long questionId,
        ChoiceType choiceType
) {
}
