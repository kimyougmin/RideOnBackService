package com.ll.rideon.global.security.oauth2.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.domain.users.dto.SocialLoginResponse;
import com.ll.rideon.domain.users.entity.Users;
import com.ll.rideon.global.security.custom.CustomUserDetails;
import com.ll.rideon.global.security.jwt.service.TokenManagementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 소셜 로그인 성공 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
	/**
	 * 토큰 관리 서비스
	 */
	private final TokenManagementService tokenManagementService;
	/**
	 * ObjectMapper
	 */
	private final ObjectMapper objectMapper;

	@Value("${site.url.frontend}")
	private String frontendUrl;

	/**
	 * 소셜 로그인 성공을 다루는 메서드
	 * @param req HttpServletRequest
	 * @param resp HttpServletResponse
	 * @param authentication Authentication
	 * @throws IOException 예외
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp,
		Authentication authentication) throws IOException {
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

		Users user = extractUserFromOAuth2User(oAuth2User);

		CustomUserDetails userDetails = new CustomUserDetails(user);

		String email = userDetails.getUsername();
		if (email == null || email.isEmpty()) {
			throw new OAuth2AuthenticationException("이메일 정보가 없습니다.");
		}

		tokenManagementService.createAndStoreTokens(userDetails, resp);

		SocialLoginResponse dtoResp = createSocialLoginResponse(user);

		setJsonResponse(resp, dtoResp);

		redirectToFrontend(resp, tokenManagementService.getAccessToken(), dtoResp);
	}

	/**
	 * 소셜 로그인 후 사용자 정보를 추출하여 Users 테이블에 맞게 변환
	 * @param oAuth2User 소셜 유저
	 * @return {@link Users}
	 */
	private Users extractUserFromOAuth2User(OAuth2User oAuth2User) {
		try {
			Map<String, Object> attributes = oAuth2User.getAttributes();
			String registrationId = null;

			if (attributes.containsKey("kakao_account")) {
				registrationId = "kakao";
			} else if (attributes.containsKey("email") && attributes.containsKey("sub")) {
				registrationId = "google";
			} else if (attributes.containsKey("response")) {
				registrationId = "naver";
			}

			if (registrationId == null) {
				throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다.");
			}

			Long userId = (Long)attributes.get("id");
			String name = null;
			String email = null;
			String profileImg = null;
			String phone = null;
			String providerId = null;
			String birthDate = null;

			if ("kakao".equals(registrationId)) {
				Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
				Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

				email = (String) kakaoAccount.get("email");
				name = (String)properties.get("nickname");
				profileImg = (String)properties.get("profile_image");
				providerId = String.valueOf(attributes.get("id"));
				phone = String.valueOf(kakaoAccount.get("phone_number")).replace("+82 ", "0");
				birthDate = String.valueOf(attributes.get("phone_number"));

				return Users.builder()
					.id(userId)
					.name(name)
					.email(email)
					.profileImage(profileImg)
					.userId(providerId)
					.birthDate(birthDate)
					.phone(phone)
					.build();
			} else if ("google".equals(registrationId)) {
				name = (String)attributes.get("name");
				email = (String)attributes.get("email");
				profileImg = (String)attributes.get("picture");
				providerId = String.valueOf(attributes.get("sub"));

				return Users.builder()
					.id(userId)
					.name(name)
					.email(email)
					.profileImage(profileImg)
					.userId(providerId)
					.build();
			} else {
				Map<String, Object> response = (Map<String, Object>)attributes.get("response");
				name = (String)response.get("name");
				email = (String)response.get("email");
				profileImg = (String)response.get("profile_image");
				providerId = (String)response.get("id");

				return Users.builder()
					.id(userId)
					.name(name)
					.email(email)
					.profileImage(profileImg)
					.userId(providerId)
					.build();
			}

		} catch (Exception e) {
			throw new OAuth2AuthenticationException("OAuth2 사용자 정보 변환에 실패하였습니다." + e.getMessage());
		}
	}

	/**
	 * 소셜 로그인 성공 시 출력될 dto 생성
	 * @param user 소셜 유저
	 * @return {@link SocialLoginResponse}
	 */
	private SocialLoginResponse createSocialLoginResponse(Users user) {
		return SocialLoginResponse.builder()
			.id(user.getId())
			.name(user.getName())
			.email(user.getEmail())
			.profileImage(user.getProfileImage())
			.phone(user.getPhone())
			.build();
	}

	/**
	 * 클라이언트로 보낼 데이터를 json으로 가공
	 * @param resp HttpServletResponse
	 * @param socialLoginResponse 로그인 유저 정보
	 * @throws IOException 예외
	 */
	private void setJsonResponse(HttpServletResponse resp, SocialLoginResponse socialLoginResponse) throws IOException {
		resp.setHeader("Authorization", "Bearer " + tokenManagementService.getAccessToken());
		resp.setContentType("application/json;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");

		ResponseEntity<?> response = ResponseEntity.ok(socialLoginResponse);

		resp.getWriter().write(objectMapper.writeValueAsString(response));
	}

	/**
	 * 소셜 로그인 후 프론트 페이지로 리다이렉트
	 * @param resp HttpServletResponse
	 * @param accessToken 엑세스 토큰
	 * @param userInfo 로그인 유저 정보
	 * @throws IOException 예외
	 */
	private void redirectToFrontend(HttpServletResponse resp, String accessToken, SocialLoginResponse userInfo) throws
		IOException {
		String encodedUserInfo = URLEncoder.encode(objectMapper.writeValueAsString(userInfo), StandardCharsets.UTF_8);
		String redirectUrl = String.format("%s/login?token=%s&user=%s",
			frontendUrl,
			accessToken,
			encodedUserInfo
		);

		resp.sendRedirect(redirectUrl);
	}
}
