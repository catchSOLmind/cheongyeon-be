package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record HouseworkTestQuestionsResponse(
        List<HouseworkTestQuestionResponse> questions
) {
    public static HouseworkTestQuestionsResponse of(
            List<HouseworkTestQuestionResponse> questions
    ) {
        return HouseworkTestQuestionsResponse.builder()
                .questions(questions)
                .build();
    }
}
