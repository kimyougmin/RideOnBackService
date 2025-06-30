package com.ll.rideon.domain.news.controller;

import com.ll.rideon.domain.news.dto.NewsResponseDto;
import com.ll.rideon.domain.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "뉴스", description = "네이버 뉴스 API")
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "최신순 뉴스", description = "최신 뉴스 반환")
    @ApiResponse(responseCode = "401", description = "잘못된 요청", content = @Content)
    @GetMapping("/latest")
    public ResponseEntity<List<NewsResponseDto>> getLatestNews() {
        return ResponseEntity.ok(newsService.fetchNews("date"));
    }

    @Operation(summary = "인기순 뉴스", description = "최신 뉴스 반환")
    @ApiResponse(responseCode = "401", description = "잘못된 요청", content = @Content)
    @GetMapping("/popular")
    public ResponseEntity<List<NewsResponseDto>> getPopularNews() {
        return ResponseEntity.ok(newsService.fetchNews("sim"));
    }
}
