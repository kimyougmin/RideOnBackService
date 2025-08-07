package com.ll.rideon.domain.post.dto;

import com.ll.rideon.domain.post.entity.PostComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostCommentResponseDto {

    private Long id;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;

    public static PostCommentResponseDto from(PostComment comment) {
        return PostCommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
} 