package com.catchsolmind.cheongyeonbe.domain.task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suggestion_task")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggestion_task_id")
    private Long suggestionTaskId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_type_id", nullable = false)
    private TaskType taskType;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @Column(name = "default_estimated_minutes", nullable = false)
    private Integer defaultEstimatedMinutes;

    @Column(name = "reward_point", nullable = false)
    private Integer rewardPoint; // TODO: 결제하면 포인트를 더 많이 줄수도, 제거 가능

    @Column(name = "desc_delayed", length = 500)
    private String descDelayed; // [미루어진 작업] 멘트

    @Column(name = "desc_no_assignee", length = 500)
    private String descNoAssignee; // [무담당 작업] 멘트

    @Column(name = "desc_general", length = 500)
    private String descGeneral; // [시즌 추천] 멘트

    @Column(name = "desc_repeat", length = 500)
    private String descRepeat; // [반복 작업] 멘트

    @OneToMany(mappedBy = "suggestionTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SuggestionTaskOption> options = new ArrayList<>();
}
