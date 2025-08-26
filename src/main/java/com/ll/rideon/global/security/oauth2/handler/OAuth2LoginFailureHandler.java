package com.ll.rideon.global.security.oauth2.handler;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.util.AuthResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 소셜 로그인 실패 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
	/**
	 * ObjectMapper
	 */
	private final ObjectMapper objectMapper;

	/**
	 * 소셜 로그인 실패를 다루는 메서드
	 * @param req HttpServletRequest
	 * @param resp HttpServletResponse
	 * @param exception 예외
	 * @throws IOException 예외
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp,
		AuthenticationException exception) throws IOException {
		
		// 환경변수 값 로깅
		String kakaoClientId = System.getenv("KAKAO_CLIENT_ID");
		String kakaoClientSecret = System.getenv("KAKAO_CLIENT_SECRET");
		String naverClientId = System.getenv("NAVER_CLIENT_ID");
		String naverClientSecret = System.getenv("NAVER_CLIENT_SECRET");
		
		log.error("=== OAuth2 로그인 실패 - 환경변수 확인 ===");
		log.error("KAKAO_CLIENT_ID: {}", kakaoClientId != null ? kakaoClientId.substring(0, Math.min(8, kakaoClientId.length())) + "..." : "NULL");
		log.error("KAKAO_CLIENT_SECRET: {}", kakaoClientSecret != null ? kakaoClientSecret.substring(0, Math.min(8, kakaoClientSecret.length())) + "..." : "NULL");
		log.error("NAVER_CLIENT_ID: {}", naverClientId != null ? naverClientId.substring(0, Math.min(8, naverClientId.length())) + "..." : "NULL");
		log.error("NAVER_CLIENT_SECRET: {}", naverClientSecret != null ? naverClientSecret.substring(0, Math.min(8, naverClientSecret.length())) + "..." : "NULL");
		log.error("실패 원인: {}", exception.getMessage());
		log.error("================================");
		
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		resp.setContentType("application/json;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		// 에러 응답 생성
		java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
		errorResponse.put("error", "OAuth2 로그인 실패");
		errorResponse.put("message", exception.getMessage());
		errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
		
		resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
