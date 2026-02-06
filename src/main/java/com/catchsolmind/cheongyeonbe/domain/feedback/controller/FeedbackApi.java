package com.catchsolmind.cheongyeonbe.domain.feedback.controller;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.FeedbackCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
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
            description = "피드백 작성 페이지를 로드한다."
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
}
