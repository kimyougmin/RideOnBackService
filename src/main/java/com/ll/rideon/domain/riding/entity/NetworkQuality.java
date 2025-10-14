package com.ll.rideon.domain.riding.entity;

import lombok.Getter;

@Getter
public enum NetworkQuality {
    POOR("나쁨"),
    FAIR("보통"),
    GOOD("좋음"),
    EXCELLENT("매우 좋음"),
    UNKNOWN("알 수 없음");

    private final String description;

    NetworkQuality(String description) {
        this.description = description;
    }
}
