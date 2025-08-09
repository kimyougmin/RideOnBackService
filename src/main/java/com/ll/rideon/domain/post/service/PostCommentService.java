package com.ll.rideon.domain.post.service;

import com.ll.rideon.domain.post.dto.PostCommentCreateRequestDto;
import com.ll.rideon.domain.post.dto.PostCommentResponseDto;
import com.ll.rideon.domain.post.dto.PostCommentUpdateRequestDto;
import com.ll.rideon.domain.post.entity.PostComment;
import com.ll.rideon.domain.post.repository.PostCommentRepository;
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
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final MetricsService metricsService;

    @Transactional
    public PostCommentResponseDto createComment(Long postId, PostCommentCreateRequestDto requestDto) {
        Timer.Sample timer = metricsService.startCommentCreationTimer();
        
        try {
            // 게시글이 존재하는지 확인
            if (!postRepository.existsById(postId)) {
                throw new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId);
            }

            PostComment comment = PostComment.builder()
                    .postId(postId)
                    .content(requestDto.getContent())
                    .build();

            PostComment savedComment = postCommentRepository.save(comment);
            
            // 메트릭 기록
            metricsService.incrementCommentCreated();
            
            log.info("댓글 생성 완료: ID={}, 게시글={}", savedComment.getId(), postId);
            return PostCommentResponseDto.from(savedComment);
        } finally {
            metricsService.stopCommentCreationTimer(timer);
        }
    }

    public PostCommentResponseDto getComment(Long commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        log.debug("댓글 조회: ID={}", commentId);
        return PostCommentResponseDto.from(comment);
    }

    public Page<PostCommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        // 게시글이 존재하는지 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId);
        }

        Page<PostComment> comments = postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);
        return comments.map(PostCommentResponseDto::from);
    }

    @Transactional
    public PostCommentResponseDto updateComment(Long commentId, Long postId, PostCommentUpdateRequestDto requestDto) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("댓글과 게시글이 일치하지 않습니다.");
        }

        comment.update(requestDto.getContent());
        
        // 메트릭 기록
        metricsService.incrementCommentUpdated();
        
        log.info("댓글 수정 완료: ID={}, 게시글={}", commentId, postId);
        return PostCommentResponseDto.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long postId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("댓글과 게시글이 일치하지 않습니다.");
        }

        postCommentRepository.delete(comment);
        
        // 메트릭 기록
        metricsService.incrementCommentDeleted();
        
        log.info("댓글 삭제 완료: ID={}, 게시글={}", commentId, postId);
    }
} 