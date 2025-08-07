package com.ll.rideon.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "게시글 작성 요청 DTO")
public class PostCreateRequestDto {

    @Schema(description = "게시글 제목", example = "오늘의 라이딩 후기", maxLength = 100)
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "오늘 한강변에서 라이딩을 했는데 정말 좋았습니다. 날씨도 좋고 바람도 시원해서 완벽한 라이딩이었어요!")
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @Schema(description = "게시글 이미지 URL", example = "https://example.com/images/riding-photo.jpg", maxLength = 500)
    @NotBlank(message = "이미지는 필수입니다.")
    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다.")
    private String image;
} 