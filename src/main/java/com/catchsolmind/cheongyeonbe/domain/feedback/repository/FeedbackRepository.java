package com.catchsolmind.cheongyeonbe.domain.feedback.repository;

import com.catchsolmind.cheongyeonbe.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
