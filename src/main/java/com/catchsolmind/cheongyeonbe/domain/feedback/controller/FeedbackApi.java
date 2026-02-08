package com.catchsolmind.cheongyeonbe.domain.feedback.controller;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.FeedbackCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.ReportResponse;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Feedback", description = "피드백")
public interface FeedbackApi {
    @Operation(
            summary = "피드백 작성",
            description = "피드백 작성 페이지를 로드한다.\n\n" +
                    "성향테스트 결과가 없을 경우 NULL을 반환한다.\n\n" +
                    "칭찬 스탬프: \n\n" +
                    "- DETAIL_KING: 꼼꼼왕\n\n" +
                    "- TIME_KEEPER: 시간 엄수\n\n" +
                    "- DUST_KILLER: 먼지 킬러\n\n" +
                    "- SCENT_KING: 향기왕\n\n" +
                    "- POINT_KING: 포인트왕\n\n" +
                    "- ORGANIZING_KING: 정리왕"
    )
    ApiResponse<FeedbackResponse> getFeedback(
            @Parameter(hidden = true) JwtUserDetails principal
    );

    @Operation(
            summary = "피드백 제출",
            description = "칭찬 스티커(필수)와 개선 피드백(선택)을 제출한다."
    )
    ApiResponse<Void> postFeedback(
            @Parameter(hidden = true) JwtUserDetails principal,
            @RequestBody @Valid FeedbackCreateRequest request
    );

    @Operation(
            summary = "주간 리포트 조회",
            description = "이번 주의 그룹 리포트(타이틀, 요약)와 내 피드백, 멤버별 최신 피드백을 조회한다.\n\n" +
                    "- **period**: '2026년 2월 1주차' 형식\n\n" +
                    "- **myImprovements**: 내가 받은 개선 피드백 리스트\n\n" +
                    "- **memberFeedbacks**: 나를 제외한 그룹 멤버들의 최신 피드백 1건씩"
    )
    ApiResponse<ReportResponse> getReport(
            @Parameter(hidden = true) JwtUserDetails principal
    );
}
