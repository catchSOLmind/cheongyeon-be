package com.catchsolmind.cheongyeonbe.domain.agreement.service;

import com.catchsolmind.cheongyeonbe.domain.agreement.dto.request.AgreementCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.agreement.dto.request.AgreementUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.agreement.dto.response.*;
import com.catchsolmind.cheongyeonbe.domain.agreement.entity.Agreement;
import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementItem;
import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementSign;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementItemRepository;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementRepository;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementSignRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.global.enums.AgreementStatus;
import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.enums.SignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final AgreementItemRepository agreementItemRepository;
    private final AgreementSignRepository agreementSignRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * 협약서 초안 생성
     * - OWNER만 생성 가능
     * - 그룹 멤버 수 1~100명 필수 (시연용 확장)
     */
    public AgreementCreateResponse createAgreement(GroupMember member, AgreementCreateRequest request) {
        // 1. OWNER 권한 확인
        if (member.getRole() != MemberRole.OWNER) {
            throw new IllegalArgumentException("협약서는 그룹 대표자만 생성할 수 있습니다.");
        }

        Group group = member.getGroup();

        // 2. 멤버 수 확인 (2~100명, 시연용으로 확장)
        List<GroupMember> activeMembers = groupMemberRepository.findByGroup_GroupIdAndStatusNot(
                group.getGroupId(), MemberStatus.LEFT);
        if (activeMembers.size() > 100) {
            throw new IllegalArgumentException("그룹 멤버 수는 1~100명이어야 합니다. 현재: " + activeMembers.size() + "명");
        }

        // 3. 기존 협약서 확인
        if (agreementRepository.findByGroup_GroupIdAndDeletedAtIsNull(group.getGroupId()).isPresent()) {
            throw new IllegalArgumentException("이미 협약서가 존재합니다.");
        }

        // 4. 마감일 검증
        LocalDate deadline = LocalDate.parse(request.getDeadline());
        if (deadline.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("마감일은 오늘 이후여야 합니다.");
        }

        // 5. 협약서 생성
        Agreement agreement = Agreement.builder()
                .group(group)
                .title(request.getHouseName() + " 협약서")
                .status(AgreementStatus.DRAFT)
                .deadline(deadline)
                .houseName(request.getHouseName())
                .monthlyGoal(request.getMonthlyGoal())
                .build();
        agreementRepository.save(agreement);

        // 6. 규칙 항목 생성
        List<AgreementCreateResponse.RuleDto> ruleDtos = createRules(agreement, request.getRules());

        return AgreementCreateResponse.builder()
                .agreementId(agreement.getAgreementId())
                .status(agreement.getStatus())
                .deadline(agreement.getDeadline().toString())
                .houseName(agreement.getHouseName())
                .monthlyGoal(agreement.getMonthlyGoal())
                .rules(ruleDtos)
                .createdAt(agreement.getCreatedAt())
                .build();
    }

    /**
     * 협약서 조회
     */
    @Transactional(readOnly = true)
    public AgreementResponse getAgreement(GroupMember member) {
        Group group = member.getGroup();

        Agreement agreement = agreementRepository.findByGroup_GroupIdAndDeletedAtIsNull(group.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("협약서를 찾을 수 없습니다."));

        // 규칙 목록
        List<AgreementItem> items = agreementItemRepository.findByAgreement_AgreementIdOrderByItemOrderAsc(
                agreement.getAgreementId());
        List<AgreementResponse.RuleDto> ruleDtos = items.stream()
                .map(item -> AgreementResponse.RuleDto.builder()
                        .itemId(item.getItemId())
                        .itemOrder(item.getItemOrder())
                        .itemText(item.getItemText())
                        .build())
                .collect(Collectors.toList());

        // 멤버 서명 상태
        List<GroupMember> activeMembers = groupMemberRepository.findByGroup_GroupIdAndStatusNot(
                group.getGroupId(), MemberStatus.LEFT);
        List<AgreementSign> signs = agreementSignRepository.findByAgreement_AgreementId(agreement.getAgreementId());
        Map<Long, AgreementSign> signMap = signs.stream()
                .collect(Collectors.toMap(s -> s.getMember().getGroupMemberId(), s -> s));

        List<AgreementResponse.MemberSignDto> memberDtos = activeMembers.stream()
                .map(m -> {
                    AgreementSign sign = signMap.get(m.getGroupMemberId());
                    return AgreementResponse.MemberSignDto.builder()
                            .memberId(m.getGroupMemberId())
                            .nickname(m.getUser().getNickname())
                            .profileImageUrl(m.getUser().getProfileImg())
                            .role(m.getRole())
                            .signStatus(sign != null ? SignStatus.AGREED : SignStatus.PENDING)
                            .signedAt(sign != null ? sign.getSignedAt() : null)
                            .build();
                })
                .collect(Collectors.toList());

        return AgreementResponse.builder()
                .agreementId(agreement.getAgreementId())
                .status(agreement.getStatus())
                .deadline(agreement.getDeadline() != null ? agreement.getDeadline().toString() : null)
                .houseName(agreement.getHouseName())
                .monthlyGoal(agreement.getMonthlyGoal())
                .rules(ruleDtos)
                .members(memberDtos)
                .confirmedAt(agreement.getConfirmedAt())
                .updatedAt(agreement.getUpdatedAt())
                .build();
    }

    /**
     * 협약서 수정
     * - OWNER만 가능
     * - 수정 시 모든 서명 초기화
     */
    public AgreementUpdateResponse updateAgreement(GroupMember member, Long agreementId, AgreementUpdateRequest request) {
        // 1. OWNER 권한 확인
        if (member.getRole() != MemberRole.OWNER) {
            throw new IllegalArgumentException("협약서는 그룹 대표자만 수정할 수 있습니다.");
        }

        Group group = member.getGroup();

        // 2. 협약서 조회
        Agreement agreement = agreementRepository.findByAgreementIdAndGroup_GroupId(agreementId, group.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("협약서를 찾을 수 없습니다."));

        // 3. 이미 확정된 협약서인지 확인
        if (agreement.getStatus() == AgreementStatus.CONFIRMED) {
            throw new IllegalArgumentException("이미 확정된 협약서는 수정할 수 없습니다.");
        }

        // 4. 수정 적용
        if (request.getDeadline() != null) {
            LocalDate deadline = LocalDate.parse(request.getDeadline());
            if (deadline.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("마감일은 오늘 이후여야 합니다.");
            }
            agreement.setDeadline(deadline);
        }
        if (request.getHouseName() != null) {
            agreement.setHouseName(request.getHouseName());
            agreement.setTitle(request.getHouseName() + " 협약서");
        }
        if (request.getMonthlyGoal() != null) {
            agreement.setMonthlyGoal(request.getMonthlyGoal());
        }
        if (request.getRules() != null && !request.getRules().isEmpty()) {
            // 기존 규칙 삭제
            agreementItemRepository.deleteByAgreement_AgreementId(agreementId);
            // 새 규칙 추가
            createRules(agreement, request.getRules());
        }

        // 5. 서명 초기화
        agreementSignRepository.deleteByAgreement_AgreementId(agreementId);

        agreementRepository.save(agreement);

        return AgreementUpdateResponse.builder()
                .agreementId(agreement.getAgreementId())
                .status(agreement.getStatus())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 협약서 서명 (동의)
     */
    public AgreementSignResponse signAgreement(GroupMember member, Long agreementId) {
        Group group = member.getGroup();

        // 1. 협약서 조회
        Agreement agreement = agreementRepository.findByAgreementIdAndGroup_GroupId(agreementId, group.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("협약서를 찾을 수 없습니다."));

        // 2. 이미 확정된 협약서인지 확인
        if (agreement.getStatus() == AgreementStatus.CONFIRMED) {
            throw new IllegalArgumentException("이미 확정된 협약서입니다.");
        }

        // 3. 이미 서명했는지 확인
        if (agreementSignRepository.existsByAgreement_AgreementIdAndMember_GroupMemberId(agreementId, member.getGroupMemberId())) {
            throw new IllegalArgumentException("이미 서명하셨습니다.");
        }

        // 4. 서명 생성
        AgreementSign sign = AgreementSign.builder()
                .agreement(agreement)
                .member(member)
                .build();
        agreementSignRepository.save(sign);

        // 5. 서명 현황 조회
        List<GroupMember> activeMembers = groupMemberRepository.findByGroup_GroupIdAndStatusNot(
                group.getGroupId(), MemberStatus.LEFT);
        long signedCount = agreementSignRepository.countByAgreement_AgreementId(agreementId);
        int totalCount = activeMembers.size();
        boolean allSigned = signedCount == totalCount;

        return AgreementSignResponse.builder()
                .agreementId(agreementId)
                .memberId(member.getGroupMemberId())
                .signStatus(SignStatus.AGREED)
                .signedAt(sign.getSignedAt())
                .allSigned(allSigned)
                .signedCount((int) signedCount)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 협약서 확정
     * - OWNER만 가능
     * - 모든 멤버 서명 필요
     */
    public AgreementConfirmResponse confirmAgreement(GroupMember member, Long agreementId) {
        // 1. OWNER 권한 확인
        if (member.getRole() != MemberRole.OWNER) {
            throw new IllegalArgumentException("협약서는 그룹 대표자만 확정할 수 있습니다.");
        }

        Group group = member.getGroup();

        // 2. 협약서 조회
        Agreement agreement = agreementRepository.findByAgreementIdAndGroup_GroupId(agreementId, group.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("협약서를 찾을 수 없습니다."));

        // 3. 이미 확정된 협약서인지 확인
        if (agreement.getStatus() == AgreementStatus.CONFIRMED) {
            throw new IllegalArgumentException("이미 확정된 협약서입니다.");
        }

        // 4. 모든 멤버 서명 확인
        List<GroupMember> activeMembers = groupMemberRepository.findByGroup_GroupIdAndStatusNot(
                group.getGroupId(), MemberStatus.LEFT);
        long signedCount = agreementSignRepository.countByAgreement_AgreementId(agreementId);
        if (signedCount != activeMembers.size()) {
            throw new IllegalArgumentException("모든 멤버가 서명해야 확정할 수 있습니다. (서명: " + signedCount + "/" + activeMembers.size() + ")");
        }

        // 5. 협약서 확정
        LocalDateTime now = LocalDateTime.now();
        agreement.setStatus(AgreementStatus.CONFIRMED);
        agreement.setConfirmedAt(now);
        agreementRepository.save(agreement);

        // 6. 그룹 이름 업데이트 (협약서의 houseName으로)
        group.setName(agreement.getHouseName());

        // 7. 멤버 상태 업데이트
        for (GroupMember m : activeMembers) {
            m.setStatus(MemberStatus.AGREED);
            m.setAgreedAt(now);
            groupMemberRepository.save(m);
        }

        return AgreementConfirmResponse.builder()
                .agreementId(agreementId)
                .status(AgreementStatus.CONFIRMED)
                .confirmedAt(now)
                .houseName(agreement.getHouseName())
                .build();
    }

    /**
     * 규칙 항목 생성 헬퍼
     */
    private List<AgreementCreateResponse.RuleDto> createRules(Agreement agreement, List<String> rules) {
        return rules.stream()
                .map(ruleText -> {
                    int order = rules.indexOf(ruleText) + 1;
                    AgreementItem item = AgreementItem.builder()
                            .agreement(agreement)
                            .itemOrder(order)
                            .itemText(ruleText)
                            .build();
                    agreementItemRepository.save(item);
                    return AgreementCreateResponse.RuleDto.builder()
                            .itemId(item.getItemId())
                            .itemOrder(item.getItemOrder())
                            .itemText(item.getItemText())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
