package com.catchsolmind.cheongyeonbe.domain.houseworktest.repository;

import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestChoice;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseworkTestChoiceRepository extends JpaRepository<HouseworkTestChoice, Long> {

    List<HouseworkTestChoice> findAllByQuestion(HouseworkTestQuestion question);
}
