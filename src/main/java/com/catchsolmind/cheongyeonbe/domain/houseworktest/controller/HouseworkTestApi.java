package com.catchsolmind.cheongyeonbe.domain.houseworktest.controller;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request.HouseworkTestSubmitRequest;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestResultResponse;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "HouseworkTest", description = "가사 성향 테스트")
public interface HouseworkTestApi {
    @Operation(
            summary = "가사 성향 테스트 질문 조회",
            description = "가사 성향 테스트를 위한 9개의 질문과 각 질문의 선택지(A/B) 조회"
    )
    ApiResponse<HouseworkTestQuestionsResponse> questions();

    @Operation(
            summary = "가사 성향 테스트 제출 및 결과 생성",
            description = "사용자의 답변을 제출하고 가사 성향 테스트 결과를 계산하여 반환"
    )
    ApiResponse<HouseworkTestResultResponse> result(HouseworkTestSubmitRequest request, JwtUserDetails principal);
}
