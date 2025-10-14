package com.ll.rideon.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 프로필 정보 업데이트 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDto {
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "성별", example = "남성")
    private String gender;
    
    @Schema(description = "출생년도", example = "1990")
    private String birthDate;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
}
