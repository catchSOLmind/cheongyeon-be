package com.catchsolmind.cheongyeonbe.domain.eraser.controller;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.request.ReservationRequest;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.EraserTaskOptionsResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.PaymentInfoResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.RecommendationResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.service.EraserService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eraser")
@Slf4j
public class EraserController implements EraserApi {
    private final EraserService eraserService;

    @Override
    @GetMapping("/recommendations")
    public ApiResponse<List<RecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        List<RecommendationResponse> responses = eraserService.getRecommendations(principal.user().getUserId());
        return ApiResponse.success(responses);
    }

    @Override
    @GetMapping("/options")
    public ApiResponse<List<EraserTaskOptionsResponse>> getTaskOptions(
            @RequestParam List<Long> suggestionTaskId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        List<EraserTaskOptionsResponse> responses = eraserService.getTaskOptions(suggestionTaskId, principal.user().getUserId());
        return ApiResponse.success(responses);
    }

    @Override
    @GetMapping("/payment-info")
    public ApiResponse<PaymentInfoResponse> getPaymentInfo(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        PaymentInfoResponse response = eraserService.getPaymentInfo(principal.user().getUserId());

        return ApiResponse.success(response);
    }

    @Override
    @PostMapping("/reservation")
    public ApiResponse<Long> completeReservation(
            @RequestBody ReservationRequest request,
            @AuthenticationPrincipal JwtUserDetails principal) {
        Long reservationId = eraserService.completeReservation(request, principal.user().getUserId());
        return ApiResponse.success(null);
    }
}
