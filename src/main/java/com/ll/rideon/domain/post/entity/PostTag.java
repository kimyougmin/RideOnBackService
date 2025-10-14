package com.ll.rideon.domain.post.entity;

import com.ll.rideon.domain.common.entity.Tags;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PostTagId.class)
public class PostTag {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tags tag;

    @Builder
    public PostTag(Post post, Tags tag) {
        this.post = post;
        this.tag = tag;
    }
}