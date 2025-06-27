package com.ll.rideon.domain.users.dto;

import com.ll.rideon.domain.users.entity.Users;
import lombok.Builder;
import lombok.Getter;

/**
 * 소셜 로그인 시, 보여지는 값 dto
 */
@Getter
@Builder
public class SocialLoginResponse {
    private Long id;
    private String email;
    private String name;
    private String profileImage;
    private String status;
    private Long expertId;
    private String phone;

    public static SocialLoginResponse of(Users user) {
        return SocialLoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .phone(user.getPhone() != null ? user.getPhone() : null)
                .build();
    }
}