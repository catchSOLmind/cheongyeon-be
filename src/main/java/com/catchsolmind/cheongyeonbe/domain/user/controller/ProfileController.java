package com.catchsolmind.cheongyeonbe.domain.user.controller;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.user.dto.*;
import com.catchsolmind.cheongyeonbe.domain.user.dto.request.ProfileUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileGetResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileImageUploadResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.domain.user.service.ProfileImageService;
import com.catchsolmind.cheongyeonbe.domain.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileImageService profileImageService;
    private final UserRepository userRepository;
    //private final GroupMemberRepository groupMemberRepository;

    // 테스트용
    private User mockUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("임시 유저 없음"));
    }

//    private GroupMember mockMember() {
//        return groupMemberRepository.findByUser_UserId(1L)
//                .orElseThrow(() -> new IllegalStateException("임시 멤버 없음"));
//    }
    private GroupMember mockMember() {
        return null;
    }

    @GetMapping
    @Operation(summary = "프로필 조회")
    public ProfileGetResponse getProfile(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return profileService.getProfile(user, null);
    }

    @PatchMapping
    @Operation(summary = "프로필 수정")
    public ProfileUpdateResponse updateProfile(@RequestParam Long userId,
                                               @RequestBody ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return profileService.updateProfile(user, null, request);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업로드/변경")
    public ProfileImageUploadResponse uploadProfileImage(@RequestParam Long userId,
                                                         @RequestPart("image") MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        String url = profileImageService.upload(user, image);
        return ProfileImageUploadResponse.builder().profileImageUrl(url).build();
    }
}
