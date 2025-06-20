package com.ll.rideon.domain.member.dto.response;


import com.ll.rideon.domain.member.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProfileResponse {
    private String name;
    private String profileImage;

    public static UpdateProfileResponse of(User user) {
        return UpdateProfileResponse.builder()
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}