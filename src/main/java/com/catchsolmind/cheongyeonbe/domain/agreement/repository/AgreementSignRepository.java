package com.catchsolmind.cheongyeonbe.domain.agreement.repository;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgreementSignRepository extends JpaRepository<AgreementSign, Long> {

    List<AgreementSign> findByAgreement_AgreementId(Long agreementId);

    Optional<AgreementSign> findByAgreement_AgreementIdAndMember_GroupMemberId(Long agreementId, Long memberId);

    boolean existsByAgreement_AgreementIdAndMember_GroupMemberId(Long agreementId, Long memberId);

    void deleteByAgreement_AgreementId(Long agreementId);

    long countByAgreement_AgreementId(Long agreementId);
}
