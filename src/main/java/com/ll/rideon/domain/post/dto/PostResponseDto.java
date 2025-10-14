package com.ll.rideon.domain.post.dto;

import com.ll.rideon.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "게시글 응답 DTO")
public class PostResponseDto {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;
    
    @Schema(description = "작성자 ID", example = "1")
    private Long memberId;
    
    @Schema(description = "게시글 제목", example = "오늘의 라이딩 후기")
    private String title;
    
    @Schema(description = "게시글 내용", example = "오늘 한강변에서 라이딩을 했는데 정말 좋았습니다.")
    private String content;
    
    @Schema(description = "조회수", example = "15")
    private Long viewCount;
    
    @Schema(description = "좋아요 수", example = "3")
    private Long likeCount;
    
    @Schema(description = "작성일시", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시", example = "2024-01-01T12:00:00")
    private LocalDateTime updatedAt;

    public static PostResponseDto from(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .memberId(post.getMemberId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
} 