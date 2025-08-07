package com.ll.rideon.domain.post.service;

import com.ll.rideon.domain.post.dto.PostCreateRequestDto;
import com.ll.rideon.domain.post.dto.PostResponseDto;
import com.ll.rideon.domain.post.dto.PostUpdateRequestDto;
import com.ll.rideon.domain.post.entity.Post;
import com.ll.rideon.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto requestDto, Long userId) {
        Post post = Post.builder()
                .userId(userId)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .image(requestDto.getImage())
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponseDto.from(savedPost);
    }

    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        // 조회수 증가
        postRepository.incrementViewCount(postId);
        post.incrementViewCount();

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
    }
} 