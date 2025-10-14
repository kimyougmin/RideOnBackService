package com.ll.rideon.global.security.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.domain.members.dto.SocialLoginResponse;
import com.ll.rideon.domain.members.entity.Members;
import com.ll.rideon.global.security.custom.CustomUserDetails;
import com.ll.rideon.global.security.jwt.JwtTokenProvider;
import com.ll.rideon.global.security.jwt.service.TokenManagementService;
import com.ll.rideon.global.security.oauth2.dto.OAuthAttributes;
import com.ll.rideon.global.security.oauth2.service.CustomOAuth2UserService;
import com.ll.rideon.global.security.oauth2.service.OAuth2UserSaveService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2UserSaveService oAuth2UserSaveService;
	private final TokenManagementService tokenManagementService;
	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;

	@Value("${app.frontend-url}")
	private String frontendUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		log.info("OAuth2 로그인 성공");

		// 환경변수 값 로깅
		String kakaoClientId = System.getenv("KAKAO_CLIENT_ID");
		String kakaoClientSecret = System.getenv("KAKAO_CLIENT_SECRET");
		String naverClientId = System.getenv("NAVER_CLIENT_ID");
		String naverClientSecret = System.getenv("NAVER_CLIENT_SECRET");
		
		log.info("=== OAuth2 환경변수 확인 ===");
		log.info("KAKAO_CLIENT_ID: {}", kakaoClientId != null ? kakaoClientId.substring(0, Math.min(8, kakaoClientId.length())) + "..." : "NULL");
		log.info("KAKAO_CLIENT_SECRET: {}", kakaoClientSecret != null ? kakaoClientSecret.substring(0, Math.min(8, kakaoClientSecret.length())) + "..." : "NULL");
		log.info("NAVER_CLIENT_ID: {}", naverClientId != null ? naverClientId.substring(0, Math.min(8, naverClientId.length())) + "..." : "NULL");
		log.info("NAVER_CLIENT_SECRET: {}", naverClientSecret != null ? naverClientSecret.substring(0, Math.min(8, naverClientSecret.length())) + "..." : "NULL");
		log.info("==========================");

		try {
			OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
			Members user = extractUserFromOAuth2User(oAuth2User);

			// 사용자 저장 또는 업데이트 (OAuth2의 경우 별도 처리 필요)
			// 여기서는 간단히 user를 그대로 사용하지만, 실제로는 OAuth2 사용자 정보를 적절히 처리해야 합니다
			Members savedUser = user;

			CustomUserDetails userDetails = new CustomUserDetails(savedUser);

			String email = userDetails.getUsername();
			if (email == null || email.isEmpty()) {
				throw new OAuth2AuthenticationException("이메일 정보가 없습니다.");
			}

			// JWT 토큰 생성 및 저장
			String accessToken = jwtTokenProvider.createAccessToken(email, savedUser.getId(), email);
			String refreshToken = jwtTokenProvider.createRefreshToken(email, savedUser.getId(), email);
			
			// 리프레시 토큰 저장
			tokenManagementService.saveRefreshToken(email, refreshToken);

			SocialLoginResponse dtoResp = createSocialLoginResponse(savedUser);

			setJsonResponse(response, dtoResp, accessToken);

			redirectToFrontend(response, accessToken, dtoResp);
		} catch (Exception e) {
			log.error("OAuth2 로그인 처리 중 오류 발생", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 로그인 처리 중 오류가 발생했습니다.");
		}
	}

	/**
	 * 소셜 로그인 후 사용자 정보를 추출하여 Users 테이블에 맞게 변환
	 * @param oAuth2User 소셜 유저
	 * @return {@link Users}
	 */
	private Members extractUserFromOAuth2User(OAuth2User oAuth2User) {
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

			Long memberId = (Long)attributes.get("id");
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

				// 프로필 이미지가 없는 경우 기본 이미지 설정
				if (profileImg == null || profileImg.isEmpty()) {
					profileImg = "https://via.placeholder.com/150x150/4A90E2/FFFFFF?text=User";
				}

				return Members.builder()
					.id(memberId)
					.name(name)
					.email(email)
					.profileImage(profileImg)
                    
                    .birthDate(birthDate != null && !birthDate.isBlank() ? java.time.LocalDate.parse(birthDate) : null)
					.phone(phone)
					.build();
			} else if ("google".equals(registrationId)) {
				name = (String)attributes.get("name");
				email = (String)attributes.get("email");
				profileImg = (String)attributes.get("picture");
				providerId = String.valueOf(attributes.get("sub"));

				// 프로필 이미지가 없는 경우 기본 이미지 설정
				if (profileImg == null || profileImg.isEmpty()) {
					profileImg = "https://via.placeholder.com/150x150/4A90E2/FFFFFF?text=User";
				}

				return Members.builder()
					.id(memberId)
					.name(name)
					.email(email)
					.profileImage(profileImg)
                    
					.build();
			} else {
				Map<String, Object> response = (Map<String, Object>)attributes.get("response");
				name = (String)response.get("name");
				email = (String)response.get("email");
				profileImg = (String)response.get("profile_image");
				providerId = (String)response.get("id");

				// 프로필 이미지가 없는 경우 기본 이미지 설정
				if (profileImg == null || profileImg.isEmpty()) {
					profileImg = "https://via.placeholder.com/150x150/4A90E2/FFFFFF?text=User";
				}

				return Members.builder()
					.id(memberId)
					.name(name)
					.email(email)
					.profileImage(profileImg)
                    
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
	private SocialLoginResponse createSocialLoginResponse(Members member) {
		return SocialLoginResponse.builder()
			.id(member.getId())
			.name(member.getName())
			.email(member.getEmail())
			.profileImage(member.getProfileImage())
			.phone(member.getPhone())
			.build();
	}

	/**
	 * 클라이언트로 보낼 데이터를 json으로 가공
	 * @param resp HttpServletResponse
	 * @param socialLoginResponse 로그인 유저 정보
	 * @param accessToken 액세스 토큰
	 * @throws IOException 예외
	 */
	private void setJsonResponse(HttpServletResponse resp, SocialLoginResponse socialLoginResponse, String accessToken) throws IOException {
		resp.setHeader("Authorization", "Bearer " + accessToken);
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
