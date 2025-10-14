package com.ll.rideon.domain.question.entity;

import com.ll.rideon.domain.common.entity.Tags;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(QuestionTagId.class)
public class QuestionTag {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tags tag;

    @Builder
    public QuestionTag(Question question, Tags tag) {
        this.question = question;
        this.tag = tag;
    }
}
