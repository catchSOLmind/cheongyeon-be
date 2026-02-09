package com.catchsolmind.cheongyeonbe.domain.point.repository;

import com.catchsolmind.cheongyeonbe.domain.point.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
}
