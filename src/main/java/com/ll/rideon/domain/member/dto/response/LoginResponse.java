package com.ll.rideon.domain.member.dto.response;

import com.ll.rideon.domain.member.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 시, 보여지는 데이터 dto
 */
@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private String phone;

    public static LoginResponse of(User user) {
        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .phone(user.getPhone() != null ? user.getPhone() : null)
                .build();
    }
}
