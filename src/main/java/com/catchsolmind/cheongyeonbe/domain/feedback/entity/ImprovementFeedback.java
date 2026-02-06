package com.catchsolmind.cheongyeonbe.domain.feedback.entity;

import com.catchsolmind.cheongyeonbe.global.enums.AiStatus;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "improvement_feedback")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ImprovementFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskCategory category;

    @Column(name = "raw_text", nullable = false, columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "ai_text", columnDefinition = "TEXT")
    private String aiText;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_status")
    @Builder.Default
    private AiStatus aiStatus = AiStatus.UNCOMPLETED;
}
