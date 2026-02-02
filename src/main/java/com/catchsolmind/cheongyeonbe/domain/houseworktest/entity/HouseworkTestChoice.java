package com.catchsolmind.cheongyeonbe.domain.houseworktest.entity;

import com.catchsolmind.cheongyeonbe.global.enums.ChoiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 선택에 대한 가중치

@Entity
@Table(name = "housework_test_choice",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"question_id", "choice_type"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseworkTestChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long choiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private HouseworkTestQuestion question;

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    private ChoiceType choiceType; // A, B 중 선택

    @Column(nullable = false)
    private String content; // 선택지 문구

    @Column(nullable = false)
    private int activeScore; // 행동성

    @Column(nullable = false)
    private int cleanScore; // 꼼꼼함

    @Column(nullable = false)
    private int routineScore; // 유지력

    @Column(nullable = false)
    private int sloppyScore; // 대충력
}
