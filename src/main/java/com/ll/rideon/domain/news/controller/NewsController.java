package com.ll.rideon.domain.news.controller;

import com.ll.rideon.domain.news.dto.NewsResponseDto;
import com.ll.rideon.domain.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "📰 뉴스", description = "자전거 관련 뉴스를 조회하는 API")
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    private final NewsService newsService;

    @Operation(
            summary = "📰 최신순 뉴스",
            description = """
                    자전거 관련 최신 뉴스를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 자전거 관련 최신 뉴스를 날짜순으로 조회합니다
                    - 네이버 뉴스 API를 통해 실시간 뉴스를 가져옵니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📰 뉴스 정보
                    - 제목, 원본 링크, 네이버 링크
                    - 뉴스 요약 내용
                    - 발행일시
                    - 최대 6개의 뉴스 제공
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/news/latest
                    ```
                    
                    ## 🔍 응답 정보
                    - 자전거 관련 최신 뉴스 목록
                    - 각 뉴스의 제목, 링크, 요약, 발행일시
                    
                    ## ⚠️ 주의사항
                    - 네이버 API 설정이 없으면 목업 데이터가 반환됩니다
                    - 뉴스는 실시간으로 업데이트됩니다
                    - 네이버 API 호출 제한이 있을 수 있습니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최신 뉴스 조회 성공",
                    content = @Content(schema = @Schema(implementation = NewsResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "뉴스 API 호출 실패 (목업 데이터 반환)")
    })
    @GetMapping("/latest")
    public ResponseEntity<List<NewsResponseDto>> getLatestNews() {
        log.info("최신 뉴스 조회 시도");
        
        try {
            List<NewsResponseDto> news = newsService.fetchNews("date");
            log.info("최신 뉴스 조회 성공 - 뉴스 수: {}", news.size());
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            log.error("최신 뉴스 조회 실패 - 오류: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "🔥 인기순 뉴스",
            description = """
                    자전거 관련 인기 뉴스를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 자전거 관련 인기 뉴스를 관련도순으로 조회합니다
                    - 네이버 뉴스 API를 통해 인기 뉴스를 가져옵니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📰 뉴스 정보
                    - 제목, 원본 링크, 네이버 링크
                    - 뉴스 요약 내용
                    - 발행일시
                    - 최대 6개의 뉴스 제공
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/news/popular
                    ```
                    
                    ## 🔍 응답 정보
                    - 자전거 관련 인기 뉴스 목록
                    - 각 뉴스의 제목, 링크, 요약, 발행일시
                    
                    ## ⚠️ 주의사항
                    - 네이버 API 설정이 없으면 목업 데이터가 반환됩니다
                    - 인기도는 네이버의 알고리즘에 따라 결정됩니다
                    - 네이버 API 호출 제한이 있을 수 있습니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 뉴스 조회 성공",
                    content = @Content(schema = @Schema(implementation = NewsResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "뉴스 API 호출 실패 (목업 데이터 반환)")
    })
    @GetMapping("/popular")
    public ResponseEntity<List<NewsResponseDto>> getPopularNews() {
        log.info("인기 뉴스 조회 시도");
        
        try {
            List<NewsResponseDto> news = newsService.fetchNews("sim");
            log.info("인기 뉴스 조회 성공 - 뉴스 수: {}", news.size());
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            log.error("인기 뉴스 조회 실패 - 오류: {}", e.getMessage(), e);
            throw e;
        }
    }
}
