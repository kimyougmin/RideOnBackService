package com.ll.rideon.domain.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.domain.news.dto.NewsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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
            URI uri = UriComponentsBuilder.fromHttpUrl(NAVER_NEWS_API)
                    .queryParam("query", "자전거")
                    .queryParam("display", 10)
                    .queryParam("start", 1)
                    .queryParam("sort", sort)  // date or sim
                    .build().toUri();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(response.body()).get("items");

            List<NewsResponseDto> result = new ArrayList<>();
            for (JsonNode item : items) {
                result.add(NewsResponseDto.builder()
                        .title(item.get("title").asText())
                        .originallink(item.get("originallink").asText())
                        .link(item.get("link").asText())
                        .description(item.get("description").asText())
                        .pubDate(item.get("pubDate").asText())
                        .build());
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
