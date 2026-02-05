package com.catchsolmind.cheongyeonbe.domain.eraser.controller;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.request.ReservationRequest;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.EraserTaskOptionsResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.PaymentInfoResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.RecommendationResponse;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Eraser", description = "청연 지우개")
public interface EraserApi {
    @Operation(
            summary = "추천 업무 목록 조회",
            description = "청연 지우개 버튼 클릭 시 이번 달 누적 절약 시간, 청연 지우개 포인트, 추천 업무를 보여준다."
    )
    ApiResponse<List<RecommendationResponse>> getRecommendations(
            @Parameter(hidden = true) JwtUserDetails principal
    );

    @Operation(
            summary = "옵션 선택",
            description = "선택한 추천 업무들의 옵션 목록을 조회한다. \n\n" +
                    " options - DELAYED: 미루어진 작업, NO_ASSIGNEE: 무담당 작업, GENERAL: 시즌 추천, REPEAT: 반복 작업"
    )
    ApiResponse<List<EraserTaskOptionsResponse>> getTaskOptions(
            @RequestParam List<Long> suggestionTaskId,
            @Parameter(hidden = true) JwtUserDetails principal
    );

    @Operation(
            summary = "결제 정보",
            description = "사용자의 포인트를 조회한다. "
    )
    ApiResponse<PaymentInfoResponse> getPaymentInfo(
            @Parameter(hidden = true) JwtUserDetails principal
    );

    @Operation(
            summary = "예약 완료 (결제)",
            description = "예약 정보를 저장하고 결제를 확정한다. "
    )
    ApiResponse<Long> completeReservation(
            ReservationRequest request,
            @Parameter(hidden = true) JwtUserDetails principal
    );
}
