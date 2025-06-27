package com.ll.rideon.global.security.oauth2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.rideon.domain.users.entity.Users;
import com.ll.rideon.domain.users.repository.UserRepository;
import com.ll.rideon.global.security.oauth2.dto.OAuthAttributes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2UserSaveService {
	private final UserRepository userRepository;

	@Transactional
	public Users saveIfNotExist(OAuthAttributes attributes) {
		Users user = null;

		// userId로 사용자 조회
		if (attributes.getUserId() != null) {
			user = userRepository.findByUserId(attributes.getUserId()).orElse(null);
		}

		// 이메일로 사용자 조회
		if (user == null && attributes.getEmail() != null) {
			user = userRepository.findByEmail(attributes.getEmail()).orElse(null);
		}

		if (user == null) {
			user = attributes.toEntity();
			user = userRepository.save(user);
		}

		return user;
	}
}
