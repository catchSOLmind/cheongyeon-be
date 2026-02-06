package com.catchsolmind.cheongyeonbe.domain.task.repository;

import com.catchsolmind.cheongyeonbe.domain.task.entity.PersonalityRecommendTask;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonalityRecommendTaskRepository extends JpaRepository<PersonalityRecommendTask, Long> {

    // 성향 + 카테고리로 추천 업무 조회
    @Query("SELECT prt FROM PersonalityRecommendTask prt " +
            "JOIN FETCH prt.taskType tt " +
            "WHERE prt.personalityType = :personalityType " +
            "AND tt.category = :category")
    List<PersonalityRecommendTask> findByPersonalityTypeAndCategory(
            @Param("personalityType") TestResultType personalityType,
            @Param("category") TaskCategory category
    );

    // 성향 + 카테고리 + 서브카테고리로 추천 업무 조회 (ETC용)
    @Query("SELECT prt FROM PersonalityRecommendTask prt " +
            "JOIN FETCH prt.taskType tt " +
            "WHERE prt.personalityType = :personalityType " +
            "AND tt.category = :category " +
            "AND tt.subCategory = :subCategory")
    List<PersonalityRecommendTask> findByPersonalityTypeAndCategoryAndSubCategory(
            @Param("personalityType") TestResultType personalityType,
            @Param("category") TaskCategory category,
            @Param("subCategory") com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory subCategory
    );
}
