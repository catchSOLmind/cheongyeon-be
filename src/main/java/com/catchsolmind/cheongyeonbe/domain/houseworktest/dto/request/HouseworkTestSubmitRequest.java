package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record HouseworkTestSubmitRequest(
        @NotEmpty List<HouseworkTestAnswerRequest> answers
) {
}
