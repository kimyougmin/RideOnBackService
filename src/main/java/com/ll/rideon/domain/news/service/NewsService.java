package com.ll.rideon.domain.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.domain.news.dto.NewsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private static final String NAVER_NEWS_API = "https://openapi.naver.com/v1/search/news.json";

    public List<NewsResponseDto> fetchNews(String sort) {
        try {
            // 환경변수 확인
            if ("test".equals(clientId) || "test".equals(clientSecret)) {
                log.warn("네이버 API 클라이언트 정보가 설정되지 않았습니다. 환경변수 NAVER_CLIENT_ID, NAVER_CLIENT_SECRET을 확인해주세요.");
                return getMockNewsData();
            }

            log.info("네이버 뉴스 API 호출 시작 - sort: {}, clientId: {}", sort, clientId.substring(0, Math.min(10, clientId.length())) + "...");

            URI uri = UriComponentsBuilder.fromHttpUrl(NAVER_NEWS_API)
                    .queryParam("query", "자전거")
                    .queryParam("display", 6)
                    .queryParam("start", 1)
                    .queryParam("sort", sort)  // "date" or "sim"
                    .build().toUri();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("네이버 API 응답 상태: {}", response.statusCode());

            if (response.statusCode() != 200) {
                log.error("네이버 API 호출 실패 - 상태코드: {}, 응답: {}", response.statusCode(), response.body());
                return getMockNewsData();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            // 에러 응답 확인
            if (root.has("errorCode") || root.has("errorMessage")) {
                log.error("네이버 API 에러 응답: {}", root.toPrettyString());
                return getMockNewsData();
            }

            JsonNode items = root.get("items");
            if (items == null || !items.isArray()) {
                log.error("items가 null이거나 배열이 아님: {}", root.toPrettyString());
                return getMockNewsData();
            }

            List<NewsResponseDto> result = new ArrayList<>();
            for (JsonNode item : items) {
                result.add(NewsResponseDto.builder()
                        .title(item.get("title").asText(""))
                        .originallink(item.get("originallink").asText(""))
                        .link(item.get("link").asText(""))
                        .description(item.get("description").asText(""))
                        .pubDate(item.get("pubDate").asText(""))
                        .build());
            }

            log.info("뉴스 데이터 조회 완료 - {}개 항목", result.size());
            return result;

        } catch (Exception e) {
            log.error("뉴스 API 호출 중 오류 발생", e);
            return getMockNewsData();
        }
    }

    /**
     * 네이버 API 설정이 되지 않았을 때 사용할 목업 데이터
     */
    private List<NewsResponseDto> getMockNewsData() {
        List<NewsResponseDto> mockData = new ArrayList<>();
        
        mockData.add(NewsResponseDto.builder()
                .title("자전거 도로 확장 프로젝트 시작")
                .originallink("https://example.com/news1")
                .link("https://example.com/news1")
                .description("서울시에서 자전거 도로 확장 프로젝트를 시작했습니다. 시민들의 안전한 자전거 이용을 위한 대규모 사업입니다.")
                .pubDate("2024-01-15")
                .build());
        
        mockData.add(NewsResponseDto.builder()
                .title("전기자전거 보조금 지원 확대")
                .originallink("https://example.com/news2")
                .link("https://example.com/news2")
                .description("정부에서 전기자전거 구매 보조금 지원을 확대한다고 발표했습니다. 친환경 교통수단 이용을 장려하기 위한 조치입니다.")
                .pubDate("2024-01-14")
                .build());
        
        mockData.add(NewsResponseDto.builder()
                .title("자전거 안전 교육 프로그램 운영")
                .originallink("https://example.com/news3")
                .link("https://example.com/news3")
                .description("초등학교에서 자전거 안전 교육 프로그램을 운영합니다. 어린이들의 안전한 자전거 이용을 위한 교육입니다.")
                .pubDate("2024-01-13")
                .build());
        
        mockData.add(NewsResponseDto.builder()
                .title("자전거 공유 서비스 인기 상승")
                .originallink("https://example.com/news4")
                .link("https://example.com/news4")
                .description("도시에서 자전거 공유 서비스 이용률이 크게 증가했습니다. 편리하고 친환경적인 교통수단으로 주목받고 있습니다.")
                .pubDate("2024-01-12")
                .build());
        
        mockData.add(NewsResponseDto.builder()
                .title("자전거 도로 정비 사업 완료")
                .originallink("https://example.com/news5")
                .link("https://example.com/news5")
                .description("강남구에서 자전거 도로 정비 사업이 완료되었습니다. 더욱 안전하고 편리한 자전거 이용 환경을 제공합니다.")
                .pubDate("2024-01-11")
                .build());
        
        mockData.add(NewsResponseDto.builder()
                .title("자전거 문화 축제 개최 예정")
                .originallink("https://example.com/news6")
                .link("https://example.com/news6")
                .description("올해 봄에 대규모 자전거 문화 축제가 개최될 예정입니다. 다양한 자전거 관련 행사와 체험 프로그램이 준비되어 있습니다.")
                .pubDate("2024-01-10")
                .build());
        
        return mockData;
    }
}
