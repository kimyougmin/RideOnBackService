package com.ll.rideon.domain.post.repository;

import com.ll.rideon.domain.post.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("SELECT pc FROM PostComment pc WHERE pc.postId = :postId ORDER BY pc.createdAt ASC")
    Page<PostComment> findByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId, Pageable pageable);

    boolean existsByIdAndPostId(Long id, Long postId);
} 