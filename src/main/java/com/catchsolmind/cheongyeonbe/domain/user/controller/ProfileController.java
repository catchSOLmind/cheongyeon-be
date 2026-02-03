package com.catchsolmind.cheongyeonbe.domain.user.controller;

import com.catchsolmind.cheongyeonbe.domain.user.dto.request.ProfileUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileGetResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileImageUploadResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.user.service.ProfileImageService;
import com.catchsolmind.cheongyeonbe.domain.user.service.ProfileService;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileImageService profileImageService;

    private void validatePrincipal(@AuthenticationPrincipal JwtUserDetails principal) {
        if (principal == null) {
            log.error("[Auth] @AuthenticationPrincipal 주입 실패: principal is null");
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
        log.info("[Auth] 인증 사용자 확인: ID={}, Nickname={}",
                principal.user().getUserId(),
                principal.user().getNickname());
    }

    @GetMapping
    @Operation(summary = "프로필 조회")
    public ProfileGetResponse getProfile(@AuthenticationPrincipal JwtUserDetails principal) {
        validatePrincipal(principal);
        return profileService.getProfile(principal.user(), null);
    }

    @PatchMapping
    @Operation(summary = "프로필 수정")
    public ProfileUpdateResponse updateProfile(
            @AuthenticationPrincipal JwtUserDetails principal,
            @RequestBody ProfileUpdateRequest request
    ) {
        validatePrincipal(principal);
        return profileService.updateProfile(principal.user(), null, request);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업로드/변경")
    public ProfileImageUploadResponse uploadProfileImage(
            @AuthenticationPrincipal JwtUserDetails principal,
            @RequestPart("image") MultipartFile image
    ) {
        validatePrincipal(principal);
        String url = profileImageService.upload(principal.user(), image);
        return ProfileImageUploadResponse.builder()
                .profileImageUrl(url)
                .build();
    }
}
