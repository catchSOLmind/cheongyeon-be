package com.catchsolmind.cheongyeonbe.domain.auth.dto.response;

/*
 * - 참고: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
 * - 카카오 API 호출용 토큰 정보
 * - 카카오 JSON 키와 필드명이 동일해야 함
 */

import lombok.Builder;

@Builder
public record KakaoTokenResponse(

        String token_type,

        String access_token,

        Integer expires_in,

        String refresh_token,

        Integer refresh_token_expires_in
) {
}
