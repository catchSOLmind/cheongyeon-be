package com.catchsolmind.cheongyeonbe.domain.user.controller;

import com.catchsolmind.cheongyeonbe.domain.user.dto.request.ProfileUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileGetResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileImageUploadResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.service.ProfileImageService;
import com.catchsolmind.cheongyeonbe.domain.user.service.ProfileService;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileImageService profileImageService;

    @GetMapping
    @Operation(summary = "프로필 조회")
    public ProfileGetResponse getProfile(@AuthenticationPrincipal JwtUserDetails principal) {
        return profileService.getProfile(principal.user());
    }

    @PatchMapping
    @Operation(summary = "프로필 수정")
    public ProfileUpdateResponse updateProfile(
            @AuthenticationPrincipal JwtUserDetails principal,
            @RequestBody ProfileUpdateRequest request
    ) {
        return profileService.updateProfile(principal.user(), null, request);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업로드/변경")
    public ProfileImageUploadResponse uploadProfileImage(
            @AuthenticationPrincipal JwtUserDetails principal,
            @RequestPart("image") MultipartFile image
    ) {
        String url = profileImageService.upload(principal.user(), image);
        return ProfileImageUploadResponse.builder()
                .profileImageUrl(url)
                .build();
    }
}
