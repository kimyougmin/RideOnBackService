package com.ll.rideon.global.security.oauth2.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.ll.rideon.domain.members.entity.ProviderType;
import com.ll.rideon.domain.members.entity.Members;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 소셜 로그인 유저 정보 dto
 */
@Getter
@ToString(exclude = "attributes")
public class OAuthAttributes {
	/**
	 * 이름
	 */
	private final String name;
	/**
	 * 프로필 사진
	 */
	private final String profileImage;
	/**
	 * 소셜 ID
	 */
	private final Long id;
	/**
	 * 이메일
	 */
	private final String email;
	/**
	 * 전화번호
	 */
	private final String phone;
	/**
	 * 생년월일
	 */
	private final String birthDate;
	/**
	 * 소셜 로그인 제공자
	 */
	private final String provider;
	/**
	 * 소셜 로그인 유저 정보
	 */
	private final Map<String, Object> attributes;

	/**
	 *
	 * @param name 이름
	 * @param profileImage 프로필 사진
	 * @param id 소셜 ID
	 * @param email 이메일
	 * @param provider 소셜 로그인 제공자
	 * @param attributes 소셜 로그인 유저 정보
	 */
	@Builder
	public OAuthAttributes(String name, String profileImage, Long id, String email, String provider, String phone, String birthDate,
		Map<String, Object> attributes) {
		this.name = name;
		this.profileImage = profileImage;
		this.id = id;
		this.email = email;
		this.phone = phone;
		this.birthDate = birthDate;
		this.provider = provider;
		this.attributes = attributes;
	}

	/**
	 * 엔티티로 변환
	 * @return {@link Members}
	 */
	public Members toEntity() {
		String userEmail = email;
		if (userEmail == null) {
			userEmail = id + "@" + provider + ".com";
		}

		return Members.builder()
			.name(name)
			.email(userEmail)
			.password(UUID.randomUUID().toString())
			.phone(phone.replace("+82 ", "0"))
			.profileImage(profileImage)
            .birthDate(birthDate != null && !birthDate.isBlank() ? java.time.LocalDate.parse(birthDate) : null)
			.createdAt(LocalDateTime.now())
			.provider(ProviderType.valueOf(provider))
			.build();
	}
}
