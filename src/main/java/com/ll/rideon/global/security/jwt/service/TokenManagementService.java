package com.ll.rideon.global.security.jwt.service;

import com.ll.rideon.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenManagementService {

    private final JwtTokenProvider jwtTokenProvider;
    
    // 메모리에 리프레시 토큰을 저장 (실제 운영에서는 Redis 사용 권장)
    private final ConcurrentHashMap<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    /**
     * 리프레시 토큰을 저장합니다.
     * @param userId 사용자 ID
     * @param refreshToken 리프레시 토큰
     */
    public void saveRefreshToken(String userId, String refreshToken) {
        refreshTokenStore.put(userId, refreshToken);
        log.info("리프레시 토큰 저장 완료 - 사용자: {}", userId);
    }

    /**
     * 사용자의 리프레시 토큰을 조회합니다.
     * @param userId 사용자 ID
     * @return 리프레시 토큰 (없으면 null)
     */
    public String getRefreshToken(String userId) {
        return refreshTokenStore.get(userId);
    }

    /**
     * 리프레시 토큰을 삭제합니다.
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(String userId) {
        refreshTokenStore.remove(userId);
        log.info("리프레시 토큰 삭제 완료 - 사용자: {}", userId);
    }

    /**
     * 리프레시 토큰이 유효한지 확인합니다.
     * @param refreshToken 리프레시 토큰
     * @return 유효성 여부
     */
    public boolean isValidRefreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return false;
        }

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            return false;
        }

        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            return false;
        }

        // 저장된 토큰과 일치하는지 확인
        String userId = jwtTokenProvider.getEmailFromToken(refreshToken);
        String storedToken = getRefreshToken(userId);
        return refreshToken.equals(storedToken);
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 생성합니다.
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰 (실패 시 null)
     */
    public String refreshAccessToken(String refreshToken) {
        if (!isValidRefreshToken(refreshToken)) {
            log.warn("유효하지 않은 리프레시 토큰");
            return null;
        }

        try {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            Long id = jwtTokenProvider.getIdFromToken(refreshToken);
            
            // 새로운 액세스 토큰 생성
            String newAccessToken = jwtTokenProvider.createAccessToken(email, id, email);
            log.info("새로운 액세스 토큰 생성 완료 - 사용자: {}", email);
            
            return newAccessToken;
        } catch (Exception e) {
            log.error("액세스 토큰 갱신 실패", e);
            return null;
        }
    }

    /**
     * 사용자 로그아웃 시 모든 토큰을 삭제합니다.
     * @param userId 사용자 ID
     */
    public void logout(String userId) {
        deleteRefreshToken(userId);
        log.info("사용자 로그아웃 완료 - 사용자: {}", userId);
    }
}