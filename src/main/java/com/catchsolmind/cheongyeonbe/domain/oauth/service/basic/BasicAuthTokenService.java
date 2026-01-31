package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.request.RefreshTokenRequest;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.RefreshTokenResponse;
import com.catchsolmind.cheongyeonbe.domain.oauth.entity.RefreshToken;
import com.catchsolmind.cheongyeonbe.domain.oauth.repository.RefreshTokenRepository;
import com.catchsolmind.cheongyeonbe.domain.oauth.service.AuthTokenService;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthTokenService implements AuthTokenService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
//    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        // 기본 JWT 검증
        jwtProvider.validateToken(refreshToken);
        jwtProvider.validateRefreshToken(refreshToken);

        // userId 추출
        Long userId = Long.valueOf(
                jwtProvider.parseClaims(refreshToken).getSubject()
        );

        // DB에 저장된 Refresh Token 조회
        RefreshToken savedToken = refreshTokenRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        // 토큰 값 비교
        if (!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 기존 Refresh Token 삭제
        refreshTokenRepository.delete(savedToken);

        // 새 토큰 발급
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getUserId());

        // 새 Refresh Token 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .refreshToken(newRefreshToken)
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusDays(14))
                        .build()
        );

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn((int) (jwtProvider.getAccessTokenExpirationMs() / 1000))
                .refreshToken(newRefreshToken)
                .refreshTokenExpiresIn((int) (jwtProvider.getRefreshTokenExpirationMs() / 1000))
                .build();
    }
}
