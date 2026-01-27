package com.catchsolmind.cheongyeonbe.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_preference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long preferenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private GroupMember member;

    @Column(name = "preferred_time_slots", columnDefinition = "JSON")
    private String preferredTimeSlots; // JSON 문자열: ["morning", "evening"]

    @Column(name = "personality_type", length = 50)
    private String personalityType;

    @Column(name = "test_result", columnDefinition = "JSON")
    private String testResult; // JSON 문자열

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}