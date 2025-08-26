package com.ll.rideon.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuthConfig implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("=== OAuth 환경변수 초기 확인 ===");
        
        // 카카오 OAuth 설정 확인
        String kakaoClientId = System.getenv("KAKAO_CLIENT_ID");
        String kakaoClientSecret = System.getenv("KAKAO_CLIENT_SECRET");
        
        log.info("KAKAO_CLIENT_ID: {}", 
            kakaoClientId != null ? 
            kakaoClientId.substring(0, Math.min(8, kakaoClientId.length())) + "..." : 
            "NULL (설정되지 않음)");
            
        log.info("KAKAO_CLIENT_SECRET: {}", 
            kakaoClientSecret != null ? 
            kakaoClientSecret.substring(0, Math.min(8, kakaoClientSecret.length())) + "..." : 
            "NULL (설정되지 않음)");
        
        // 네이버 OAuth 설정 확인
        String naverClientId = System.getenv("NAVER_CLIENT_ID");
        String naverClientSecret = System.getenv("NAVER_CLIENT_SECRET");
        
        log.info("NAVER_CLIENT_ID: {}", 
            naverClientId != null ? 
            naverClientId.substring(0, Math.min(8, naverClientId.length())) + "..." : 
            "NULL (설정되지 않음)");
            
        log.info("NAVER_CLIENT_SECRET: {}", 
            naverClientSecret != null ? 
            naverClientSecret.substring(0, Math.min(8, naverClientSecret.length())) + "..." : 
            "NULL (설정되지 않음)");
        
        // 구글 OAuth 설정 확인
        String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
        String googleClientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
        
        log.info("GOOGLE_CLIENT_ID: {}", 
            googleClientId != null ? 
            googleClientId.substring(0, Math.min(8, googleClientId.length())) + "..." : 
            "NULL (설정되지 않음)");
            
        log.info("GOOGLE_CLIENT_SECRET: {}", 
            googleClientSecret != null ? 
            googleClientSecret.substring(0, Math.min(8, googleClientSecret.length())) + "..." : 
            "NULL (설정되지 않음)");
        
        // JWT 설정 확인
        String jwtKey = System.getenv("JWT_KEY");
        log.info("JWT_KEY: {}", 
            jwtKey != null ? 
            jwtKey.substring(0, Math.min(8, jwtKey.length())) + "..." : 
            "NULL (설정되지 않음)");
        
        log.info("================================");
        
        // 환경변수 누락 시 경고
        if (kakaoClientId == null || kakaoClientSecret == null) {
            log.warn("⚠️ 카카오 OAuth 환경변수가 설정되지 않았습니다!");
        }
        if (naverClientId == null || naverClientSecret == null) {
            log.warn("⚠️ 네이버 OAuth 환경변수가 설정되지 않았습니다!");
        }
        if (googleClientId == null || googleClientSecret == null) {
            log.warn("⚠️ 구글 OAuth 환경변수가 설정되지 않았습니다!");
        }
        if (jwtKey == null) {
            log.warn("⚠️ JWT_KEY 환경변수가 설정되지 않았습니다!");
        }
    }
}
