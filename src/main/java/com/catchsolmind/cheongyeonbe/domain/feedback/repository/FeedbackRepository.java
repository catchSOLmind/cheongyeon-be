package com.catchsolmind.cheongyeonbe.domain.feedback.repository;

import com.catchsolmind.cheongyeonbe.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // 특정 그룹의, 특정 기간(시작일~종료일) 내 작성된 모든 피드백 조회
    @Query("SELECT f FROM Feedback f " +
            "WHERE f.group.groupId = :groupId " +
            "AND f.createdAt BETWEEN :startDate AND :endDate")
    List<Feedback> findAllByGroupIdAndDateRange(
            @Param("groupId") Long groupId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
