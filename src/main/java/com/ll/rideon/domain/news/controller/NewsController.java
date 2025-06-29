package com.ll.rideon.domain.news.controller;

import com.ll.rideon.domain.news.dto.NewsResponseDto;
import com.ll.rideon.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/latest")
    public ResponseEntity<List<NewsResponseDto>> getLatestNews() {
        return ResponseEntity.ok(newsService.fetchNews("date"));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<NewsResponseDto>> getPopularNews() {
        return ResponseEntity.ok(newsService.fetchNews("sim"));
    }
}
