package com.catchsolmind.cheongyeonbe.domain.agreement.repository;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgreementItemRepository extends JpaRepository<AgreementItem, Long> {

    List<AgreementItem> findByAgreement_AgreementIdOrderByItemOrderAsc(Long agreementId);

    void deleteByAgreement_AgreementId(Long agreementId);
}
