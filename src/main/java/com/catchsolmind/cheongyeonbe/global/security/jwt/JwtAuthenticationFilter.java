package com.catchsolmind.cheongyeonbe.global.security.jwt;

import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.enums.JwtTokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * Authorization 헤더에서 토큰 추출
 * 토큰 검증
 * ACCESS 토큰만 SecurityContext에 인증 등록
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final JwtUserDetailsService jwtUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        if (token != null) {
            try {
                jwtProvider.validateToken(token);
            } catch (BusinessException e) {
                log.error("[Auth] 토큰 검증 실패: {}", e.getErrorCode());
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtProvider.parseClaims(token);

            String tokenType = claims.get("type", String.class);
            if (!JwtTokenType.ACCESS.name().equals(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = Long.valueOf(claims.getSubject());

            JwtUserDetails userDetails =
                    jwtUserDetailsService.loadUserByUserId(userId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.toLowerCase().startsWith("bearer ")) {
                return bearerToken.substring(7).trim();
            }
        }
        return null;
    }
}
