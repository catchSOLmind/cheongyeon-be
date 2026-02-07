package com.catchsolmind.cheongyeonbe.domain.feedback.controller;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.FeedbackCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.service.FeedbackService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class FeedbackController implements FeedbackApi {
    private final FeedbackService feedbackService;

    @Override
    @GetMapping
    public ApiResponse<FeedbackResponse> getFeedback(@AuthenticationPrincipal JwtUserDetails principal) {

        FeedbackResponse response = feedbackService.getFeedback(principal.user().getUserId());

        return ApiResponse.success(response);
    }

    @Override
    @PostMapping
    public ApiResponse<Void> postFeedback(@AuthenticationPrincipal JwtUserDetails principal,
                                          FeedbackCreateRequest request) {
        return null;
    }
}
