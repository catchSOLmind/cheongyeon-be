package com.catchsolmind.cheongyeonbe.domain.oauth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "refresh:";

    public void save(Long userId, String refreshToken, long ttlMs) {
        redisTemplate.opsForValue().set(
                PREFIX + userId,
                refreshToken,
                ttlMs,
                TimeUnit.MILLISECONDS
        );
    }

    public String findByUserId(Long userId) {
        return redisTemplate.opsForValue()
                .get(PREFIX + userId);
    }

    public void delete(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}

