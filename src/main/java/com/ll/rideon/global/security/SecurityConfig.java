package com.ll.rideon.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.global.security.custom.CustomUserDetailsService;
import com.ll.rideon.global.security.jwt.JwtAuthenticationFilter;
import com.ll.rideon.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import com.ll.rideon.global.security.oauth2.handler.OAuth2LoginFailureHandler;
import com.ll.rideon.global.security.oauth2.handler.OAuth2LoginSuccessHandler;
import com.ll.rideon.global.security.oauth2.service.CustomOAuth2UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Map<HttpMethod, List<String>> PUBLIC_URLS = new HashMap<>();
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    public static Map<HttpMethod, List<String>> getPublicUrls() {
        return PUBLIC_URLS;
    }
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(authz -> authz
                        // 기본 공개 경로
                        .requestMatchers(
                                "/",
                                "/api/oauth2/**",
                                "/api/auth/**",       // 로그인 관련 모든 경로 (GET, POST 모두 허용)
                                "/api/users/register", // 회원가입
                                "/api/users/login",   // 로그인
                                "/api/test/**",       // 테스트용 API
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/favicon.ico",
                                "/error",
                                "/actuator/**",
                                "/h2-console/**"      // H2 콘솔 접근 허용
                        ).permitAll()
                        
                        // GET 메소드 - 인증 없이 접근 가능 (더 구체적인 경로를 먼저 설정)
                        .requestMatchers(HttpMethod.GET, "/api/news/**").permitAll()            // 뉴스 조회
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()           // 커뮤니티 게시글 조회
                        .requestMatchers(HttpMethod.GET, "/api/v1/obstacles/**").permitAll()    // 장애물 신고 조회
                        .requestMatchers(HttpMethod.GET, "/api/network/**").permitAll()         // 네트워크 추적 조회
                        .requestMatchers(HttpMethod.GET, "/api/riding/**").permitAll()          // 라이딩 기록 조회
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()           // 사용자 정보 조회
                        .requestMatchers(HttpMethod.GET, "/api/questions/**").permitAll()       // 질문 조회
                        .requestMatchers(HttpMethod.GET, "/api/inquiries/**").permitAll()       // 문의 조회
                        
                        // POST/PUT/DELETE 메소드 - 인증 필요
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated()      // 게시글 작성
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()       // 게시글 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()    // 게시글 삭제
                        .requestMatchers(HttpMethod.POST, "/api/v1/obstacles/**").authenticated() // 장애물 신고 작성
                        .requestMatchers(HttpMethod.PUT, "/api/v1/obstacles/**").authenticated()  // 장애물 신고 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/obstacles/**").authenticated() // 장애물 신고 삭제
                        .requestMatchers(HttpMethod.POST, "/api/riding/**").authenticated()     // 라이딩 시작/종료
                        .requestMatchers(HttpMethod.PUT, "/api/riding/**").authenticated()      // 라이딩 정보 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/riding/**").authenticated()   // 라이딩 기록 삭제
                        .requestMatchers(HttpMethod.POST, "/api/questions/**").authenticated()  // 질문 작성
                        .requestMatchers(HttpMethod.PUT, "/api/questions/**").authenticated()   // 질문 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/questions/**").authenticated() // 질문 삭제
                        .requestMatchers(HttpMethod.POST, "/api/inquiries/**").authenticated()  // 문의 작성
                        .requestMatchers(HttpMethod.PUT, "/api/inquiries/**").authenticated()   // 문의 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/inquiries/**").authenticated() // 문의 삭제
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()       // 사용자 정보 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()    // 사용자 삭제
                        
                        // 기타 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler))
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new ObjectMapper().writeValueAsString(
                    Map.of(
                            "error", "Unauthorized",
                            "message", authException.getMessage()
                    )
            ));
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

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

        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(
                Arrays.asList("Authorization", "Set-Cookie", "Access-Control-Allow-Credentials"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // ✅ reactive 아님
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
