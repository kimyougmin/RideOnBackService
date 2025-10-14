package com.ll.rideon.domain.question.entity;

import com.ll.rideon.domain.common.entity.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTagId implements Serializable {
    private Question question;
    private Tags tag;
}
