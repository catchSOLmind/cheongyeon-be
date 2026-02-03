package com.catchsolmind.cheongyeonbe.domain.houseworktest.repository;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseworkTestResultRepository extends JpaRepository<HouseworkTestResult, Long> {
    Optional<HouseworkTestResult> findByUser(User user);
}
