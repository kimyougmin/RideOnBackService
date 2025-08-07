package com.ll.rideon.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🚴‍♂️ RideOn API")
                        .description("""
                                ## 🚴‍♂️ RideOn - 자전거 라이딩 커뮤니티 API
                                
                                ### 📋 주요 기능
                                - **커뮤니티**: 게시글 작성, 수정, 삭제, 댓글 기능
                                - **라이딩 추적**: 실시간 위치 추적 및 라이딩 세션 관리
                                - **네트워크 모니터링**: 네트워크 상태 감지 및 권장사항 제공
                                - **오프라인 지원**: 네트워크 불안정 시 오프라인 데이터 동기화
                                
                                ### 🔐 인증 요구사항
                                - 게시글 작성/수정/삭제 및 댓글 기능은 로그인 필요
                                - 라이딩 관련 모든 기능은 로그인 필요
                                - 게시글 조회는 비로그인 사용자도 가능
                                
                                ### 🛠️ 사용 방법
                                1. 먼저 `/api/auth/register`로 회원가입
                                2. `/api/auth/login`으로 로그인하여 토큰 획득
                                3. 인증이 필요한 API 호출 시 Authorization 헤더에 토큰 포함
                                
                                ### 📱 모바일 환경 지원
                                - 실시간 위치 추적
                                - 네트워크 상태 모니터링
                                - 오프라인 데이터 동기화
                                - 배터리 레벨 및 신호 강도 추적
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RideOn 개발팀")
                                .email("dev@rideon.com")
                                .url("https://rideon.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("개발 서버"),
                        new Server().url("https://api.rideon.com").description("운영 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요. 로그인 후 발급받은 토큰을 사용합니다.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
} 