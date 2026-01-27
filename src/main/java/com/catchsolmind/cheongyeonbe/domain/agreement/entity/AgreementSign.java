package com.catchsolmind.cheongyeonbe.domain.agreement.entity;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "agreement_sign", uniqueConstraints = {
        @UniqueConstraint(name = "uk_agreement_member", columnNames = {"agreement_id", "member_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgreementSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sign_id")
    private Long signId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private Agreement agreement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private GroupMember member;

    @CreationTimestamp
    @Column(name = "signed_at", updatable = false)
    private LocalDateTime signedAt;
}