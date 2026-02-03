package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import software.amazon.awssdk.annotations.NotNull;

public record HouseworkTestAnswerRequest(
        @NotNull Long questionId,
        @NotNull ChoiceType choiceType
) {
}
