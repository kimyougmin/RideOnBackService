package com.ll.rideon.domain.members.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 소셜 로그인 제공자 타입
 */
public enum ProviderType {
    original,
    kakao,
    naver,
    google;

    @JsonCreator
    public static ProviderType from(String value) {
        if (value == null) return null;
        return ProviderType.valueOf(value);
    }
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
