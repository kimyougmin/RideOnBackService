package com.ll.rideon.domain.users.dto;

import com.ll.rideon.domain.users.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 소셜 로그인 시, 보여지는 값 dto
 */
@Slf4j
@Getter
@Builder
public class SocialLoginResponse {
    private Long id;
    private String email;
    private String name;
    private String profileImage;
    private String phone;

    public static SocialLoginResponse of(Users user) {
        return SocialLoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .phone(user.getPhone())
                .build();
    }
}