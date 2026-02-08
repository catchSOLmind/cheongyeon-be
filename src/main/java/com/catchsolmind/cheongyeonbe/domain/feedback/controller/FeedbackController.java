package com.catchsolmind.cheongyeonbe.domain.feedback.controller;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.FeedbackCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.ReportResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.service.FeedbackService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class FeedbackController implements FeedbackApi {
    private final FeedbackService feedbackService;

    @Override
    @GetMapping
    public ApiResponse<FeedbackResponse> getFeedback(@AuthenticationPrincipal JwtUserDetails principal) {

        FeedbackResponse response = feedbackService.getFeedback(principal.user().getUserId());

        return ApiResponse.success("피드백 조회 성공", response);
    }

    @Override
    @PostMapping
    public ApiResponse<Void> postFeedback(@AuthenticationPrincipal JwtUserDetails principal,
                                          @Valid @RequestBody FeedbackCreateRequest request) {

        feedbackService.postFeedback(principal.user().getUserId(), request);

        return ApiResponse.success("피드백 제출 성공", null);
    }

    @Override
    @GetMapping("/report")
    public ApiResponse<ReportResponse> getReport(@AuthenticationPrincipal JwtUserDetails principal) {

//        ReportResponse response = feedbackService.getReport(principal.user().getUserId());

//        return ApiResponse.success("리포트 조회 성공", response);
        return ApiResponse.success("리포트 조회 성공", null);
    }
}
