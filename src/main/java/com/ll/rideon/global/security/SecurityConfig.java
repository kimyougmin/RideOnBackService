package com.ll.rideon.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        // CORS 설정
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:8080",
                        "http://localhost:3000",
                        "http://211.197.178.97:11200",
                        "https://211.197.178.97:11200",
                        "http://www.rideon.kro.kr",
                        "https://www.rideon.kro.kr"
                )
        );

        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));

        configuration.setExposedHeaders(
                Arrays.asList("Authorization", "Set-Cookie", "Access-Control-Allow-Credentials"));

        // CORS 설정을 소스에 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
