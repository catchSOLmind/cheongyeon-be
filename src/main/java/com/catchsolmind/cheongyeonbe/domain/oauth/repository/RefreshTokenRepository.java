package com.catchsolmind.cheongyeonbe.domain.oauth.repository;

import com.catchsolmind.cheongyeonbe.domain.oauth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
