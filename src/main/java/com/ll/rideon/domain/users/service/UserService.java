package com.ll.rideon.domain.users.service;

import com.ll.rideon.domain.users.dto.*;
import com.ll.rideon.domain.users.entity.AuthProvider;
import com.ll.rideon.domain.users.entity.Users;
import com.ll.rideon.domain.users.repository.UserRepository;
import com.ll.rideon.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 회원가입 처리
     */
    public void register(UserRegisterDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // provider가 null이거나 빈 문자열인 경우 기본값으로 'original' 설정
        String providerValue = dto.getProvider();
        if (providerValue == null || providerValue.trim().isEmpty()) {
            providerValue = "original";
        }

        // 유효한 provider 값인지 확인
        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(providerValue);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid provider value: {}, using default provider: original", providerValue);
            provider = AuthProvider.original;
        }

        Users users = Users.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate())
                .userId(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .provider(provider)
                .build();

        userRepository.save(users);
        log.info("User registered successfully: {}", dto.getEmail());
    }
    /**
     * 로그인 처리 및 JWT 토큰 반환
     */
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        Users users = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), users.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(users.getName(), users.getId(), users.getUserId());

        return UserLoginResponseDto.builder()
                .token(token)
                .email(users.getEmail())
                .name(users.getName())
                .profileImage(users.getProfileImage())
                .phone(users.getPhone())
                .build();
    }
}
