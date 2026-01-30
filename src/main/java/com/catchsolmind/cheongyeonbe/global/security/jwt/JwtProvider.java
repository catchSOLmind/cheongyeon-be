package com.catchsolmind.cheongyeonbe.global.security.jwt;

/*
 * JwtProvider 역할
 * 1. Access Token 생성
 * 2. Refresh Token 생성
 * 3. 토큰 파싱 (claims 추출)
 * 4. 토큰 유효성 검증
 * 5. 토큰 만료 여부 확인
 */

import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.config.JwtProperties;
import com.catchsolmind.cheongyeonbe.global.enums.JwtTokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    // Access Token 생성
    public String createAccessToken(Long userId) {
        return createToken(
                userId,
                jwtProperties.getAccessTokenExpiration(),
                JwtTokenType.ACCESS
        );
    }

    // Refresh Token 생성
    public String createRefreshToken(Long userId) {
        return createToken(
                userId,
                jwtProperties.getRefreshTokenExpiration(),
                JwtTokenType.REFRESH
        );
    }

    // 시간
    public long getAccessTokenExpirationMs() {
        return jwtProperties.getAccessTokenExpiration();
    }

    public long getRefreshTokenExpirationMs() {
        return jwtProperties.getRefreshTokenExpiration();
    }

    // 토큰 검증
    public void validateToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    // Refresh Token 검증
    public void validateRefreshToken(String token) {
        Claims claims = parseClaims(token);

        String type = claims.get("type", String.class);
        if (!JwtTokenType.REFRESH.name().equals(type)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }


    // Claims 추출
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 공통 메서드
    private String createToken(
            Long userId, // Subject
            long expiration,
            JwtTokenType tokenType
    ) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", tokenType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256);

        return builder.compact();
    }
}
