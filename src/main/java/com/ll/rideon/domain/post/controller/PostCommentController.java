package com.ll.rideon.domain.post.controller;

import com.ll.rideon.domain.post.dto.PostCommentCreateRequestDto;
import com.ll.rideon.domain.post.dto.PostCommentResponseDto;
import com.ll.rideon.domain.post.dto.PostCommentUpdateRequestDto;
import com.ll.rideon.domain.post.service.PostCommentService;
import com.ll.rideon.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "💬 게시글 댓글", description = "게시글 댓글 작성, 조회, 수정, 삭제 API")
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping
    @Operation(
            summary = "💬 댓글 작성",
            description = "게시글에 새로운 댓글을 작성합니다. 로그인이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
                    content = @Content(schema = @Schema(implementation = PostCommentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostCommentResponseDto> createComment(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "댓글 작성 정보", required = true)
            @Validated @RequestBody PostCommentCreateRequestDto requestDto) {
        
        log.info("댓글 작성 시도 - 게시글 ID: {}, 내용: {}", postId, requestDto.getContent());
        
        try {
            PostCommentResponseDto responseDto = postCommentService.createComment(postId, requestDto);
            log.info("댓글 작성 성공 - 댓글 ID: {}, 게시글 ID: {}", responseDto.getId(), postId);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            log.error("댓글 작성 실패 - 게시글 ID: {}, 오류: {}", postId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{commentId}")
    @Operation(
            summary = "💬 댓글 조회",
            description = "특정 댓글의 상세 정보를 조회합니다. 로그인 없이도 조회 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostCommentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    public ResponseEntity<PostCommentResponseDto> getComment(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "댓글 ID", required = true, example = "1")
            @PathVariable Long commentId) {
        
        PostCommentResponseDto responseDto = postCommentService.getComment(commentId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(
            summary = "📋 댓글 목록 조회",
            description = "특정 게시글의 모든 댓글을 페이지네이션으로 조회합니다. 로그인 없이도 조회 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostCommentResponseDto.class)))
    })
    public ResponseEntity<Page<PostCommentResponseDto>> getCommentsByPostId(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "페이지 정보 (기본값: size=20)")
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<PostCommentResponseDto> comments = postCommentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    @Operation(
            summary = "✏️ 댓글 수정",
            description = "기존 댓글을 수정합니다. 본인이 작성한 댓글만 수정 가능합니다. 로그인이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(schema = @Schema(implementation = PostCommentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인 댓글만 수정 가능)"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    public ResponseEntity<PostCommentResponseDto> updateComment(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "댓글 ID", required = true, example = "1")
            @PathVariable Long commentId,
            @Parameter(description = "댓글 수정 정보", required = true)
            @Validated @RequestBody PostCommentUpdateRequestDto requestDto) {
        
        PostCommentResponseDto responseDto = postCommentService.updateComment(commentId, postId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "🗑️ 댓글 삭제",
            description = "댓글을 삭제합니다. 본인이 작성한 댓글만 삭제 가능합니다. 로그인이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인 댓글만 삭제 가능)"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "댓글 ID", required = true, example = "1")
            @PathVariable Long commentId) {
        
        postCommentService.deleteComment(commentId, postId);
        return ResponseEntity.noContent().build();
    }
} 