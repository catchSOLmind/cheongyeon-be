package com.catchsolmind.cheongyeonbe.domain.task.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.global.enums.IncompleteReasonCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_incomplete_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskIncompleteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incomplete_log_id")
    private Long incompleteLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occurrence_id", nullable = false)
    private TaskOccurrence occurrence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private GroupMember member;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_code", nullable = false)
    private IncompleteReasonCode reasonCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
