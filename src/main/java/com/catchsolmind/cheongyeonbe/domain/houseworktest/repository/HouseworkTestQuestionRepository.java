package com.catchsolmind.cheongyeonbe.domain.houseworktest.repository;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseworkTestQuestionRepository extends JpaRepository<HouseworkTestQuestion, Long> {

    List<HouseworkTestQuestion> findAllByOrderByQuestionOrderAsc();
}
