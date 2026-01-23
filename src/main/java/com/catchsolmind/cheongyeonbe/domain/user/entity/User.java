package com.catchsolmind.cheongyeonbe.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.security.AuthProvider;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_provider_provider_id", columnNames = {"provider", "provider_id"})
        },
        indexes = {
                @Index(name = "ix_users_provider_provider_id", columnList = "provider, provider_id"),
                @Index(name = "ix_users_created_at", columnList = "created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    @Column(name = "profile_img", length = 500)
    private String profileImg;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String nickname, String profileImg) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        this.profileImg = profileImg;
    }

    public static User create(AuthProvider provider, String providerId, String nickname, String profileImg) {
        return User.builder()
                .provider(provider)
                .providerId(providerId)
                .nickname(nickname)
                .profileImg(profileImg)
                .build();
    }
}

