package com.ll.rideon.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 프로필 정보 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "성별", example = "남성")
    private String gender;
    
    @Schema(description = "출생년도", example = "1990")
    private String birthDate;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile/image.jpg")
    private String profileImage;
    
    @Schema(description = "업데이트 성공 메시지", example = "프로필 정보가 성공적으로 업데이트되었습니다.")
    private String message;
}
