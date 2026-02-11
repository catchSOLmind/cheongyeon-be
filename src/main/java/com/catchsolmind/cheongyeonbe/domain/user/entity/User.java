package com.catchsolmind.cheongyeonbe.domain.user.entity;

import com.catchsolmind.cheongyeonbe.domain.auth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.point.entity.PointTransaction;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "uk_provider_id", columnNames = {"provider", "provider_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(name = "profile_img", length = 500)
    private String profileImg;

    @Column(name = "point_balance")
    @Builder.Default
    private Integer pointBalance = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PointTransaction> pointTransactions = new ArrayList<>();

    public static User createOAuthUser(OAuthUserInfo info) {
        return User.builder()
                .provider(info.provider())
                .providerId(info.providerId())
                .email(info.email())
                .nickname(info.nickname())
                .profileImg(info.profileImageUrl())
                .build();
    }

    public void deductPoint(int amount) {
        if (amount < 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT);
        }
        int currentBalance = (this.pointBalance != null) ? this.pointBalance : 0;
        if (currentBalance < amount) {
            throw new BusinessException(ErrorCode.POINT_NOT_ENOUGH);
        }

        this.pointBalance = currentBalance - amount;
    }

    public void addPoint(int amount) {
        if (amount < 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT);
        }
        int currentBalance = (this.pointBalance != null) ? this.pointBalance : 0;
        this.pointBalance = currentBalance + amount;
    }
}