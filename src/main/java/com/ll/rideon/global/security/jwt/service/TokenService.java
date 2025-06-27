package com.ll.rideon.global.security.jwt.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    // 쿠키에서 accessToken 가져오기
    public String getAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // 예시용: accessToken 기반으로 refreshToken을 가져오는 로직 (Redis 등 활용)
    public String getRefreshToken(String accessToken) {
        // Redis 또는 DB에서 accessToken → refreshToken 맵핑을 검색해야 함
        // 예: return redisRepository.findRefreshTokenByAccessToken(accessToken);
        return null; // 구현 필요
    }
}
