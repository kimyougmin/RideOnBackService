package com.ll.rideon.global.security.oauth2.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

import com.ll.rideon.global.security.oauth2.dto.OAuthAttributes;

/**
 * 소셜 로그인 유저 변환 서비스
 */
@Slf4j
@Component
public class OAuthUserConverter {
	/**
	 * 소셜 로그인 제공자 타입에 따라 적절한 변환 메서드 호출
	 * @param registrationId 소셜 로그인 제공자
	 * @param userNameAttributeName 소셜 로그인 고유 ID
	 * @param attributes 소셜 로그인 유저 정보
	 * @return {@link OAuthAttributes}
	 */
	public OAuthAttributes convert(String registrationId, String userNameAttributeName,
		Map<String, Object> attributes) {
		if ("kakao".equals(registrationId)) {
			return ofKakao(userNameAttributeName, attributes);
		} else if ("google".equals(registrationId)) {
			return ofGoogle(userNameAttributeName, attributes);
		} else if ("naver".equals(registrationId)) {
			return ofNaver(attributes);
		}
		throw new OAuth2AuthenticationException("해당 소셜 로그인은 지원하지 않습니다.");
	}

	/**
	 * 카카오 유저 변환
	 * @param userNameAttributeName 소셜 로그인 고유 ID
	 * @param attributes 소셜 로그인 유저 정보
	 * @return {@link OAuthAttributes}
	 */
	private OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		String birthday = (String) kakaoAccount.get("birthday");
		String month = birthday.substring(0, 2);
		String day = birthday.substring(2, 4);

		return OAuthAttributes.builder()
			.name((String)properties.get("nickname"))
			.profileImage((String)properties.get("profile_image"))
			.userId(String.valueOf(attributes.get(userNameAttributeName)))
			.email((String) kakaoAccount.get("email"))
			.phone((String) kakaoAccount.get("phone_number"))
			.birthDate((String) kakaoAccount.get("birthyear") + "-" + month + "-" + day)
			.provider("kakao")
			.attributes(attributes)
			.build();
	}

	/**
	 * 구글 유저 변환
	 * @param userNameAttributeName 소셜 로그인 고유 ID
	 * @param attributes 소셜 로그인 유저 정보
	 * @return {@link OAuthAttributes}
	 */
	private OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
			.name((String)attributes.get("name"))
			.profileImage((String)attributes.get("picture"))
			.email((String)attributes.get("email"))
			.userId(String.valueOf(attributes.get(userNameAttributeName)))
			.provider("google")
			.attributes(attributes)
			.build();
	}

	/**
	 * 네이버 유저 변환
	 * @param attributes 소셜 로그인 유저 정보
	 * @return {@link OAuthAttributes}
	 */
	private OAuthAttributes ofNaver(Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>)attributes.get("response");

		return OAuthAttributes.builder()
			.name((String)response.get("name"))
			.profileImage((String)response.get("profile_image"))
			.email((String)response.get("email"))
			.userId((String)response.get("id"))
			.provider("naver")
			.attributes(attributes)
			.build();
	}
}
