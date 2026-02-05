package com.catchsolmind.cheongyeonbe.domain.agreement.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AgreementUpdateRequest {

    private String deadline;

    @Size(max = 60, message = "우리집 이름은 60자 이하로 입력해주세요.")
    private String houseName;

    @Size(max = 60, message = "한 달 목표는 60자 이하로 입력해주세요.")
    private String monthlyGoal;

    @Size(min = 1, max = 5, message = "규칙은 1~5개까지 입력 가능합니다.")
    private List<@Size(max = 60, message = "각 규칙은 60자 이하로 입력해주세요.") String> rules;
}
