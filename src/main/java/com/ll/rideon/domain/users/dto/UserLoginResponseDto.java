package com.ll.rideon.domain.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserLoginResponseDto {
    private String token;
    private String email;
    private String name;
    private String profileImage;
    private String phone;

    public UserLoginResponseDto(String token) {
    }
}