package com.catchsolmind.cheongyeonbe.domain.eraser.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "suggestion_task_option")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionTaskOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggestion_task_id", nullable = false)
    private SuggestionTask suggestionTask;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "estimated_minutes", length = 50, nullable = false)
    private Integer estimatedMinutes;

    @Column(nullable = false)
    private Integer price;
}
