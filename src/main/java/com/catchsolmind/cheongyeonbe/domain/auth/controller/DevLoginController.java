package com.catchsolmind.cheongyeonbe.domain.auth.controller;

import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.KakaoLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.ApiResponse;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dev Auth", description = "개발용 강제 로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth/dev")
@Slf4j
public class DevLoginController {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Operation(summary = "개발용 강제 로그인", description = "User ID만 알면 카카오 인증 없이 바로 토큰을 발급받습니다.")
    @GetMapping("/login/{userId}")
    public ApiResponse<KakaoLoginResponse> devLogin(@PathVariable Long userId) {

        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 토큰 생성 (JwtProvider 활용)
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // 3. User -> UserDto 변환
        // (UserDto에 from 정적 메서드나 빌더가 있다고 가정합니다. 없다면 생성자로 만드세요.)
        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .build();

        // 4. 응답 생성 (KakaoLoginResponse의 정적 팩토리 메서드 활용)
        KakaoLoginResponse response = KakaoLoginResponse.of(
                userDto,
                accessToken,
                jwtProvider.getAccessTokenExpirationMs(), // 만료 시간 조회
                refreshToken,
                jwtProvider.getRefreshTokenExpirationMs()
        );

        return ApiResponse.success(response);
    }
}
