package com.catchsolmind.cheongyeonbe.domain.task.entity;

import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personality_recommend_task",
        uniqueConstraints = @UniqueConstraint(name = "uk_personality_task", columnNames = {"personality_type", "task_type_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalityRecommendTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "personality_type", nullable = false, length = 30)
    private TestResultType personalityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_type_id", nullable = false)
    private TaskType taskType;
}
