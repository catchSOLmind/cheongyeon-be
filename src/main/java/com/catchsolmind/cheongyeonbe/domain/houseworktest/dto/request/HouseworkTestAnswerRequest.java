package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request;

import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import jakarta.validation.constraints.NotNull;

public record HouseworkTestAnswerRequest(
        @NotNull Long questionId,
        @NotNull ChoiceType choiceType
) {
}
