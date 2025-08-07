package com.ll.rideon.domain.post.controller;

import com.ll.rideon.domain.post.dto.PostCreateRequestDto;
import com.ll.rideon.domain.post.dto.PostResponseDto;
import com.ll.rideon.domain.post.dto.PostUpdateRequestDto;
import com.ll.rideon.domain.post.service.PostService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "📝 커뮤니티 게시글", description = "게시글 작성, 조회, 수정, 삭제 API")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(
            summary = "📝 게시글 작성",
            description = """
                    새로운 게시글을 작성합니다.
                    
                    ## 📋 기능 설명
                    - 커뮤니티에 새로운 게시글을 등록합니다
                    - 제목, 내용, 이미지 URL을 포함하여 작성합니다
                    - 작성자 정보는 자동으로 현재 로그인한 사용자로 설정됩니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```json
                    {
                      "title": "오늘의 라이딩 후기",
                      "content": "한강변에서 라이딩했는데 정말 좋았습니다!",
                      "image": "https://example.com/riding-photo.jpg"
                    }
                    ```
                    
                    ## ⚠️ 주의사항
                    - 제목은 100자 이하여야 합니다
                    - 이미지 URL은 500자 이하여야 합니다
                    - 모든 필드는 필수입니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (제목/내용/이미지 누락 또는 길이 초과)"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (로그인이 필요합니다)")
    })
    public ResponseEntity<PostResponseDto> createPost(
            @Parameter(description = "게시글 작성 정보 (제목, 내용, 이미지 URL)", required = true)
            @Validated @RequestBody PostCreateRequestDto requestDto) {
        
        // 임시로 고정된 userId 사용
        Long userId = 1L;
        PostResponseDto responseDto = postService.createPost(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "📖 게시글 조회",
            description = """
                    특정 게시글의 상세 정보를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 게시글 ID로 특정 게시글의 상세 정보를 조회합니다
                    - 조회 시 조회수가 자동으로 증가합니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 🔍 조회되는 정보
                    - 게시글 제목, 내용, 이미지
                    - 작성자 정보
                    - 조회수, 좋아요 수
                    - 작성일시, 수정일시
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/posts/1
                    ```
                    
                    ## ⚠️ 주의사항
                    - 존재하지 않는 게시글 ID로 요청 시 404 에러가 발생합니다
                    - 조회할 때마다 조회수가 1씩 증가합니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 (존재하지 않는 게시글 ID)")
    })
    public ResponseEntity<PostResponseDto> getPost(
            @Parameter(description = "조회할 게시글의 ID", required = true, example = "1")
            @PathVariable Long postId) {
        PostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(
            summary = "📋 게시글 목록 조회",
            description = """
                    모든 게시글의 목록을 페이지네이션으로 조회합니다.
                    
                    ## 📋 기능 설명
                    - 전체 게시글 목록을 페이지 단위로 조회합니다
                    - 최신 게시글이 먼저 표시됩니다 (생성일시 기준 내림차순)
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📄 페이지네이션
                    - 기본 페이지 크기: 10개
                    - 페이지 번호: 0부터 시작
                    - 정렬: 생성일시 기준 내림차순
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/posts?page=0&size=10
                    GET /api/posts?page=1&size=20
                    ```
                    
                    ## 🔍 응답 정보
                    - 게시글 목록 (제목, 작성자, 조회수, 좋아요 수, 작성일시)
                    - 페이지 정보 (현재 페이지, 전체 페이지 수, 전체 게시글 수)
                    
                    ## ⚠️ 주의사항
                    - 페이지 번호는 0부터 시작합니다
                    - 페이지 크기는 최대 100개까지 설정 가능합니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class)))
    })
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @Parameter(description = "페이지 정보 (page: 페이지 번호, size: 페이지 크기, 기본값: size=10)")
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostResponseDto> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "👤 사용자별 게시글 조회",
            description = "특정 사용자가 작성한 게시글 목록을 조회합니다. 로그인 없이도 조회 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 게시글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class)))
    })
    public ResponseEntity<Page<PostResponseDto>> getPostsByUserId(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "페이지 정보 (기본값: size=10)")
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostResponseDto> posts = postService.getPostsByUserId(userId, pageable);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    @Operation(
            summary = "✏️ 게시글 수정",
            description = "기존 게시글을 수정합니다. 본인이 작성한 게시글만 수정 가능합니다. 로그인이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인 게시글만 수정 가능)"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostResponseDto> updatePost(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "게시글 수정 정보", required = true)
            @Validated @RequestBody PostUpdateRequestDto requestDto) {
        
        // 임시로 고정된 userId 사용
        Long userId = 1L;
        PostResponseDto responseDto = postService.updatePost(postId, requestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}")
    @Operation(
            summary = "🗑️ 게시글 삭제",
            description = "게시글을 삭제합니다. 본인이 작성한 게시글만 삭제 가능합니다. 로그인이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인 게시글만 삭제 가능)"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId) {
        
        // 임시로 고정된 userId 사용
        Long userId = 1L;
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }
} 