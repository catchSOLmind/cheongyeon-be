package com.catchsolmind.cheongyeonbe.domain.houseworktest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 질문, 캐싱 대상

@Entity
@Table(name = "housework_test_question")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseworkTestQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(nullable = false, length = 30)
    private String content;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder; // 몇 번째 질문인지
}
