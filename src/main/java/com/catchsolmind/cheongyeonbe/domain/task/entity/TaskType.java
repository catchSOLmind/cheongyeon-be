package com.catchsolmind.cheongyeonbe.domain.task.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberFavoriteTaskType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_type_id")
    private Long taskTypeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_category")
    private TaskSubCategory subCategory;  // ETC 카테고리일 때 PET/BABY 구분, null 가능

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int point;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "taskType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "taskType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberFavoriteTaskType> favoritedBy = new ArrayList<>();
}
