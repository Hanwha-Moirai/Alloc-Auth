package com.moirai.alloc.auth.service;

import com.moirai.alloc.auth.dto.response.AuthTokens;
import com.moirai.alloc.auth.store.RefreshTokenStore;
import com.moirai.alloc.common.security.jwt.JwtTokenProvider;
import com.moirai.alloc.user.command.domain.User;
import com.moirai.alloc.user.command.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenStore tokenStore;

    public AuthTokens refresh(String refreshToken) {

        // 1. Refresh Token 유효성 검사
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validate(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Refresh Token 타입 확인
        Claims claims = jwtTokenProvider.getClaims(refreshToken);
        String typ = claims.get("typ", String.class);
        if (!"refresh".equals(typ)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        // 3. Refresh Token에서 userId 추출
        String userId = claims.getSubject();

        // 4. Redis에 저장된 refreshToken과 일치하는지 확인
        String storedToken = tokenStore.get(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        // 5. 사용자 조회
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        // 6. Access Token 재발급
        String newAccessToken = jwtTokenProvider.createAccessToken(
                userId,
                Map.of(
                        "email", user.getEmail(),
                        "role", user.getAuth().name() // Enum → String
                )
        );

        // 7. Refresh Token 회전
        String newRefreshToken = jwtTokenProvider.createRefreshToken(Long.valueOf(userId));
        tokenStore.save(userId, newRefreshToken, jwtTokenProvider.getRefreshExpSeconds());

        // 8. 응답 생성 (토큰은 쿠키로 전달)
        return new AuthTokens(newAccessToken, newRefreshToken);
    }
}
