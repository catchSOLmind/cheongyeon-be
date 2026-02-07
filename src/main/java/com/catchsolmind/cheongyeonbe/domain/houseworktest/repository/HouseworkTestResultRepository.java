package com.catchsolmind.cheongyeonbe.domain.houseworktest.repository;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseworkTestResultRepository extends JpaRepository<HouseworkTestResult, Long> {
    Optional<HouseworkTestResult> findByUser(User user);

    // 유저 리스트에 포함된 결과들을 한 번에 조회
    List<HouseworkTestResult> findByUser_UserIdIn(List<Long> userIds);
}
