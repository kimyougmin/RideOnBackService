package com.ll.rideon.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 프로필 이미지 업데이트 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileImageUpdateDto {
    
    @Schema(description = "업데이트된 프로필 이미지 URL", example = "https://example.com/profile/image.jpg")
    private String profileImageUrl;
    
    @Schema(description = "업데이트 성공 메시지", example = "프로필 이미지가 성공적으로 업데이트되었습니다.")
    private String message;
}
