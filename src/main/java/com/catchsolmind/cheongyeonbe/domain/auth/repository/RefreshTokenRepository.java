package com.catchsolmind.cheongyeonbe.domain.auth.repository;

import com.catchsolmind.cheongyeonbe.domain.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Transactional
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
