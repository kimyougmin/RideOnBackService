package com.ll.rideon.domain.post.entity;

import java.io.Serializable;
import java.util.Objects;

public class PostTagId implements Serializable {
    private Long post;
    private Long tag;

    public PostTagId() {}

    public PostTagId(Long post, Long tag) {
        this.post = post;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostTagId)) return false;
        PostTagId that = (PostTagId) o;
        return Objects.equals(post, that.post) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, tag);
    }
}