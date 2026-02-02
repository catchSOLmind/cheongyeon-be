package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestChoice;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestQuestion;
import lombok.Builder;

import java.util.List;

@Builder
public record HouseworkTestQuestionResponse(
        Long questionId,
        Integer order,
        String content,
        List<ChoiceResponse> choices
) {
    public static HouseworkTestQuestionResponse from(
            HouseworkTestQuestion question,
            List<HouseworkTestChoice> choices
    ) {
        return HouseworkTestQuestionResponse.builder()
                .questionId(question.getQuestionId())
                .order(question.getQuestionOrder())
                .content(question.getContent())
                .choices(
                        choices.stream()
                                .map(ChoiceResponse::from)
                                .toList()
                )
                .build();
    }
}
