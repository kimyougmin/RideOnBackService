package com.ll.rideon.domain.question.entity;

public enum QuestionStatus {
    UNSOLVED("미해결"),
    SOLVED("해결됨");

    private final String description;

    QuestionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
