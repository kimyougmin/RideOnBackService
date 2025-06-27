package com.ll.rideon.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ll.rideon.global.security.custom.CustomUserDetails;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;

/**
 * jwt 관련 유틸리티
 */
@Component
public class JwtUtil {
	/**
	 * jwt 시크릿 키
	 */
	private final SecretKey secretKey;
	/**
	 * 시크릿 키 부여
	 * @param secret 시크릿
	 */
	public JwtUtil(@Value("${jwt.secret}") String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 토큰에서 ID 추출
	 * @param token 토큰
	 * @return {@link Long}
	 */
	public Long getUserId(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.get("id", Long.class);
	}

	/**
	 * 토큰에서 이메일 추출
	 * @param token 토큰
	 * @return {@link String}
	 */
	public String getUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
			.get("username", String.class);
	}

	/**
	 * 토큰이 만료됐는지 확인
	 * @param token 토큰
	 * @return {@link Boolean}
	 */
	public boolean isExpired(String token) {
		Date expiration = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getExpiration();

		return expiration.before(new Date());
	}

	/**
	 * 토큰의 만료일 추출
	 * @param token 토큰
	 * @return {@link Date}
	 */
	public Date getExpirationDate(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
			.getExpiration();
	}

	/**
	 * 엑세스 토큰 생성
	 * @param customUserDetails 인증 유저 정보
	 * @param expiration 만료 기간
	 * @return {@link String}
	 */
	public String createAccessToken(CustomUserDetails customUserDetails, long expiration) {
		long currentTime = System.currentTimeMillis();

		return Jwts.builder()
				.claim("subject", "access")
				.claim("id", customUserDetails.getUsers().getId())
				.claim("username", customUserDetails.getUsername())
				.setIssuedAt(new Date(currentTime))
				.setExpiration(new Date(currentTime + expiration))
				.signWith(secretKey, SignatureAlgorithm.HS512)
				.compact();
	}

	/**
	 * 리프레시 토큰 생성
	 * @param customUserDetails 인증 유저 정보
	 * @param expiration 만료 기간
	 * @return {@link String}
	 */
	public String createRefreshToken(CustomUserDetails customUserDetails, long expiration) {
		long currentTime = System.currentTimeMillis();

		return Jwts.builder()
				.claim("subject", "refresh")
				.claim("id", customUserDetails.getUsers().getId())
				.claim("username", customUserDetails.getUsername())
				.setIssuedAt(new Date(currentTime))
				.setExpiration(new Date(currentTime + expiration))
				.signWith(secretKey, SignatureAlgorithm.HS512)
				.compact();
	}

	/**
	 * 쿠키 설정
	 * @param key key 값
	 * @param value value 값
	 * @param expiration 만료 기간
	 * @return {@link Cookie}
	 */
	public Cookie setJwtCookie(String key, String value, long expiration) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge((int)expiration / 1000);
		cookie.setSecure(false);    // HTTPS에서만 전송
		cookie.setPath("/");        // 모든 경로에서 접근 가능
		cookie.setDomain("");        // 모든 서브 도메인에서 접근 가능
		cookie.setHttpOnly(true);    // XSS 공격 방지

		return cookie;
	}
}
