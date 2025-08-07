package com.ll.rideon.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCommentCreateRequestDto {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
} 