package com.catchsolmind.cheongyeonbe.domain.agreement.controller;

import com.catchsolmind.cheongyeonbe.domain.agreement.dto.request.AgreementCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.agreement.dto.request.AgreementUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.agreement.dto.response.*;
import com.catchsolmind.cheongyeonbe.domain.agreement.service.AgreementService;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agreements")
@Tag(name = "Agreement", description = "협약서 API")
public class AgreementController {

    private final AgreementService agreementService;
    private final GroupMemberRepository groupMemberRepository;

    private GroupMember currentMember(User user) {
        return groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("그룹 미가입 사용자"));
    }

    @PostMapping
    @Operation(summary = "협약서 초안 생성", description = "그룹 OWNER만 협약서를 생성할 수 있습니다. 그룹 멤버 수 2~5명 필수")
    public ApiResponse<AgreementCreateResponse> createAgreement(
            @Valid @RequestBody AgreementCreateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        AgreementCreateResponse response = agreementService.createAgreement(member, request);
        return ApiResponse.success("협약서 초안 생성 성공", response);
    }

    @GetMapping
    @Operation(summary = "협약서 조회", description = "현재 그룹의 협약서를 조회합니다. 멤버별 서명 상태 포함")
    public ApiResponse<AgreementResponse> getAgreement(
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        AgreementResponse response = agreementService.getAgreement(member);
        return ApiResponse.success("협약서 조회 성공", response);
    }

    @PatchMapping("/{agreementId}")
    @Operation(summary = "협약서 수정", description = "그룹 OWNER만 수정 가능. 수정 시 모든 멤버의 서명이 초기화됩니다")
    public ApiResponse<AgreementUpdateResponse> updateAgreement(
            @PathVariable Long agreementId,
            @Valid @RequestBody AgreementUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        AgreementUpdateResponse response = agreementService.updateAgreement(member, agreementId, request);
        return ApiResponse.success("협약서 수정 성공", response);
    }

    @PostMapping("/{agreementId}/sign")
    @Operation(summary = "협약서 서명 (동의)", description = "협약서에 서명합니다")
    public ApiResponse<AgreementSignResponse> signAgreement(
            @PathVariable Long agreementId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        AgreementSignResponse response = agreementService.signAgreement(member, agreementId);
        return ApiResponse.success("협약서 서명 완료", response);
    }

    @PostMapping("/{agreementId}/confirm")
    @Operation(summary = "협약서 확정", description = "그룹 OWNER만 가능. 모든 멤버가 서명한 상태에서만 확정 가능")
    public ApiResponse<AgreementConfirmResponse> confirmAgreement(
            @PathVariable Long agreementId,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        User user = principal.user();
        GroupMember member = currentMember(user);
        AgreementConfirmResponse response = agreementService.confirmAgreement(member, agreementId);
        return ApiResponse.success("협약서가 확정되었습니다", response);
    }
}
