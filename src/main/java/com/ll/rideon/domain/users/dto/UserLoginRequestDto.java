package com.ll.rideon.domain.users.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {
    private String email;
    private String password;
}

