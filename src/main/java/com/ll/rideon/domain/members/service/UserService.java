package com.ll.rideon.domain.members.service;

import com.ll.rideon.domain.members.dto.*;
import com.ll.rideon.domain.members.entity.ProviderType;
import com.ll.rideon.domain.members.entity.Members;
import com.ll.rideon.domain.members.repository.UserRepository;
import com.ll.rideon.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
//        String providerValue = String.valueOf(dto.getProvider());
//        if (providerValue == null || providerValue.trim().isEmpty()) {
//            providerValue = "original";
//        }
//
//        // 유효한 provider 값인지 확인
//        ProviderType provider;
//        try {
//            provider = ProviderType.valueOf(providerValue);
//        } catch (IllegalArgumentException e) {
//            log.warn("Invalid provider value: {}, using default provider: original", providerValue);
//            provider = ProviderType.original;
//        }

        Members member = Members.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate() != null ? java.time.LocalDate.parse(dto.getBirthDate()) : null)
                
                .createdAt(LocalDateTime.now())
                .provider(ProviderType.original)
                .build();

        userRepository.save(member);
        log.info("User registered successfully: {}", dto.getEmail());
    }
    /**
     * 로그인 처리 및 JWT 토큰 반환
     */
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        Members members = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), members.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(members.getName(), members.getId(), members.getEmail());

        return UserLoginResponseDto.builder()
                .token(token)
                .email(members.getEmail())
                .name(members.getName())
                .profileImage(members.getProfileImage())
                .phone(members.getPhone())
                .build();
    }

    /**
     * 사용자 프로필 정보 업데이트
     */
    public UserProfileResponseDto updateProfile(Long userId, UserProfileUpdateDto dto) {
        Members members = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 프로필 정보 업데이트
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            members.setName(dto.getName());
        }
        if (dto.getGender() != null) {
            members.setGender(dto.getGender());
        }
        if (dto.getBirthDate() != null) {
            members.setBirthDate(java.time.LocalDate.parse(dto.getBirthDate()));
        }
        if (dto.getPhone() != null) {
            members.setPhone(dto.getPhone());
        }

        userRepository.save(members);
        log.info("User profile updated successfully: {}", members.getEmail());

        return UserProfileResponseDto.builder()
                .id(members.getId())
                .email(members.getEmail())
                .name(members.getName())
                .gender(members.getGender())
                .birthDate(members.getBirthDate() != null ? members.getBirthDate().toString() : null)
                .phone(members.getPhone())
                .profileImage(members.getProfileImage())
                .message("프로필 정보가 성공적으로 업데이트되었습니다.")
                .build();
    }

    /**
     * 사용자 프로필 이미지 업데이트 (URL)
     */
    public UserProfileImageUpdateDto updateProfileImage(Long userId, String imageUrl) {
        Members members = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 프로필 이미지 업데이트
        members.setProfileImage(imageUrl);
        userRepository.save(members);
        
        log.info("User profile image updated successfully: {}", members.getEmail());

        return UserProfileImageUpdateDto.builder()
                .profileImageUrl(imageUrl)
                .message("프로필 이미지가 성공적으로 업데이트되었습니다.")
                .build();
    }


    /**
     * 사용자 ID로 사용자 정보 조회
     */
    public Members findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
