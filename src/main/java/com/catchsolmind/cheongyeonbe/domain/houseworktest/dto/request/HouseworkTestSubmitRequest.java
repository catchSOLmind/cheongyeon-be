package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request;

import java.util.List;

public record HouseworkTestSubmitRequest(
        List<HouseworkTestAnswerRequest> answers
) {
}
