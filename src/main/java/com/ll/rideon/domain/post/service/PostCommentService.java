package com.ll.rideon.domain.post.service;

import com.ll.rideon.domain.post.dto.PostCommentCreateRequestDto;
import com.ll.rideon.domain.post.dto.PostCommentResponseDto;
import com.ll.rideon.domain.post.dto.PostCommentUpdateRequestDto;
import com.ll.rideon.domain.post.entity.PostComment;
import com.ll.rideon.domain.post.repository.PostCommentRepository;
import com.ll.rideon.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostCommentResponseDto createComment(Long postId, PostCommentCreateRequestDto requestDto) {
        // 게시글이 존재하는지 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId);
        }

        PostComment comment = PostComment.builder()
                .postId(postId)
                .content(requestDto.getContent())
                .build();

        PostComment savedComment = postCommentRepository.save(comment);
        return PostCommentResponseDto.from(savedComment);
    }

    public PostCommentResponseDto getComment(Long commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

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
    }
} 