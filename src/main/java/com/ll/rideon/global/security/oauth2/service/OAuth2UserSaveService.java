package com.ll.rideon.global.security.oauth2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.rideon.domain.members.entity.Members;
import com.ll.rideon.domain.members.repository.UserRepository;
import com.ll.rideon.global.security.oauth2.dto.OAuthAttributes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2UserSaveService {
	private final UserRepository userRepository;

	@Transactional
	public Members saveIfNotExist(OAuthAttributes attributes) {
		Members member = null;

		// userId로 사용자 조회
		if (attributes.getId() != null) {
			member = userRepository.findById(attributes.getId()).orElse(null);
		}

		// 이메일로 사용자 조회
		if (member == null && attributes.getEmail() != null) {
			member = userRepository.findByEmail(attributes.getEmail()).orElse(null);
		}

		if (member == null) {
			member = attributes.toEntity();
			member = userRepository.save(member);
		}

		return member;
	}
}
