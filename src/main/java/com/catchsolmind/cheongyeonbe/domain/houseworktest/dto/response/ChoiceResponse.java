package com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestChoice;
import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import lombok.Builder;

@Builder
public record ChoiceResponse(
        ChoiceType choiceType, // A or B
        String content
) {
    public static ChoiceResponse from(HouseworkTestChoice choice) {
        return ChoiceResponse.builder()
                .choiceType(choice.getChoiceType())
                .content(choice.getContent())
                .build();
    }
}
