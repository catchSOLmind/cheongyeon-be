package com.catchsolmind.cheongyeonbe.domain.eraser.controller;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.EraserTaskOptionsResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.RecommendationResponse;
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

    @Override
    @GetMapping("/recommendations")
    public ApiResponse<List<RecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        return ApiResponse.success(
                List.of()
        );
    }

    @Override
    @GetMapping("/options")
    public ApiResponse<List<EraserTaskOptionsResponse>> getTaskOptions(
            @RequestParam List<Long> taskIds
    ) {
        return ApiResponse.success(
                List.of()
        );
    }

//    @Override
//    @PostMapping("/reservation")
//    public ApiResponse<?> makeReservation() {
//        return null;
//    }
//
//    @Override
//    @PostMapping("/complete-reservation")
//    public ApiResponse<?> completeReservation() {
//        return null;
//    }
}
