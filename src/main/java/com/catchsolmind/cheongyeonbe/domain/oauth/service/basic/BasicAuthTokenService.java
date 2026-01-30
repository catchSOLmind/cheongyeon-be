package com.catchsolmind.cheongyeonbe.domain.oauth.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.request.RefreshTokenRequest;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.RefreshTokenResponse;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthTokenService implements AuthTokenService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        // 기본 JWT 검증 (서명, 만료)
        jwtProvider.validateToken(refreshToken);

        // Refresh 토큰인지 확인
        jwtProvider.validateRefreshToken(refreshToken);

        // userId 추출
        Long userId = Long.valueOf(
                jwtProvider.parseClaims(refreshToken).getSubject()
        );

        // Redis에 저장된 토큰과 비교
        String savedToken = refreshTokenRepository.findByUserId(userId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 새 토큰 발급
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getUserId());

        // Redis 갱신
        refreshTokenRepository.save(
                userId,
                newRefreshToken,
                jwtProvider.getRefreshTokenExpirationMs()
        );

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn((int) (jwtProvider.getAccessTokenExpirationMs() / 1000))
                .refreshToken(newRefreshToken)
                .refreshTokenExpiresIn((int) (jwtProvider.getRefreshTokenExpirationMs() / 1000))
                .build();
    }
}
