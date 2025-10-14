package com.ll.rideon.domain.members.dto;

import com.ll.rideon.domain.members.entity.Members;

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

    public static SocialLoginResponse of(Members member) {
        return SocialLoginResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .phone(member.getPhone())
                .build();
    }
}