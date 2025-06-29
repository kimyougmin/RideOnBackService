package com.ll.rideon.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewsResponseDto {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
}