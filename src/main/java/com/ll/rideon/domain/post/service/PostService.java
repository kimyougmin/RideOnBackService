package com.ll.rideon.domain.post.service;

import com.ll.rideon.domain.post.dto.PostCreateRequestDto;
import com.ll.rideon.domain.post.dto.PostResponseDto;
import com.ll.rideon.domain.post.dto.PostUpdateRequestDto;
import com.ll.rideon.domain.post.entity.Post;
import com.ll.rideon.domain.post.repository.PostRepository;
import com.ll.rideon.global.monitoring.MetricsService;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MetricsService metricsService;

    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto requestDto, Long userId) {
        Timer.Sample timer = metricsService.startPostCreationTimer();
        
        try {
            Post post = Post.builder()
                    .userId(userId)
                    .title(requestDto.getTitle())
                    .content(requestDto.getContent())
                    .image(requestDto.getImage())
                    .build();

            Post savedPost = postRepository.save(post);
            
            // 메트릭 기록
            metricsService.incrementPostCreated();
            
            log.info("게시글 생성 완료: ID={}, 사용자={}", savedPost.getId(), userId);
            return PostResponseDto.from(savedPost);
        } finally {
            metricsService.stopPostCreationTimer(timer);
        }
    }

    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        // 조회수 증가
        postRepository.incrementViewCount(postId);
        post.incrementViewCount();
        
        // 메트릭 기록
        metricsService.incrementPostViewed();

        log.debug("게시글 조회: ID={}", postId);
        return PostResponseDto.from(post);
    }

    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return posts.map(PostResponseDto::from);
    }

    public Page<PostResponseDto> getPostsByUserId(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return posts.map(PostResponseDto::from);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        post.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getImage());
        
        // 메트릭 기록
        metricsService.incrementPostUpdated();
        
        log.info("게시글 수정 완료: ID={}, 사용자={}", postId, userId);
        return PostResponseDto.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
        
        // 메트릭 기록
        metricsService.incrementPostDeleted();
        
        log.info("게시글 삭제 완료: ID={}, 사용자={}", postId, userId);
    }
} 