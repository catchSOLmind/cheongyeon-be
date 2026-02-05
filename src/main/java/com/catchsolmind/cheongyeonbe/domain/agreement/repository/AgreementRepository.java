package com.catchsolmind.cheongyeonbe.domain.agreement.repository;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgreementRepository extends JpaRepository<Agreement, Long> {

    Optional<Agreement> findByGroup_GroupIdAndDeletedAtIsNull(Long groupId);

    Optional<Agreement> findByAgreementIdAndGroup_GroupId(Long agreementId, Long groupId);
}
