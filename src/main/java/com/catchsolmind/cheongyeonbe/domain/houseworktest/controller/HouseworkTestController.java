package com.catchsolmind.cheongyeonbe.domain.houseworktest.controller;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.request.HouseworkTestSubmitRequest;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestQuestionsResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.dto.response.HouseworkTestResultResponse;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.service.HouseworkTestService;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/housework-test")
@Slf4j
public class HouseworkTestController implements HouseworkTestApi {
    private final HouseworkTestService houseworkTestService;

    @Override
    @GetMapping("/questions")
    public ApiResponse<HouseworkTestQuestionsResponse> getQuestions() {

        return ApiResponse.success(
                houseworkTestService.getQuestions()
        );
    }

    @Override
    @PostMapping("/results")
    public ApiResponse<HouseworkTestResultResponse> submitResult(

            @Valid @RequestBody HouseworkTestSubmitRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = (principal != null) ? principal.user() : null;

        return ApiResponse.success(
                houseworkTestService.submitTest(request, user)
        );
    }
}
