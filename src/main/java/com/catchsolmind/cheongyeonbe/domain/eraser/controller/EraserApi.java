package com.catchsolmind.cheongyeonbe.domain.eraser.controller;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.EraserTaskOptionsResponse;
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
            description = "선택한 추천 업무들의 옵션 목록을 조회한다."
    )
    ApiResponse<List<EraserTaskOptionsResponse>> getTaskOptions(
            @RequestParam List<Long> taskIds
    );
//
//    @Operation(
//            summary = "예약 일정 잡기",
//            description = "추천 업무 별 날짜/시간 선택"
//    )
//    ApiResponse<?> makeReservation();
//
//    @Operation(
//            summary = "예약 완료",
//            description = "추가 예정"
//    )
//    ApiResponse<?> completeReservation();
}
