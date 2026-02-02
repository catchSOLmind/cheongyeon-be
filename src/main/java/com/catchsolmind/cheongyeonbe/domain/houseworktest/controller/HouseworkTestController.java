package com.catchsolmind.cheongyeonbe.domain.houseworktest.controller;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request.HouseworkTestSubmitRequest;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestResultResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.service.HouseworkTestService;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/housework-test")
public class HouseworkTestController implements HouseworkTestApi {
    private final HouseworkTestService houseworkTestService;

    @Override
    @GetMapping("/questions")
    public ApiResponse<HouseworkTestQuestionsResponse> questions() {
        return ApiResponse.success(houseworkTestService.getQuestions());
    }

    @Override
    @PostMapping("/results")
    public ApiResponse<HouseworkTestResultResponse> result(
            @RequestBody HouseworkTestSubmitRequest request) {
        return null;
    }
}
