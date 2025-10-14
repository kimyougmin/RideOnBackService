package com.ll.rideon.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "qwer1234!")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "성별", example = "남/여")
    private String gender;

    @Schema(description = "출생년도", example = "YYYY-MM-DD")
    private String birthDate;

    @Schema(description = "전화번호", example = "010-1234-1234")
    private String phone;

    @Schema(description = "가입방법", example = "original")
    private String provider;
}
