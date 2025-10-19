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
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(restAuthenticationEntryPoint())
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/api/oauth2/**", "/api/auth/**",
                                "/api/users/register", "/api/users/login", "/api/users/register",
                                "/api/test/**", "/swagger-ui/**", "/v3/api-docs/**",
                                "/favicon.ico", "/error", "/actuator/**", "/h2-console/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/news/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/obstacles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/network/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/riding/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/questions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inquiries/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/obstacles/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/obstacles/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/obstacles/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/riding/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/images").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/riding/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/riding/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/questions/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/questions/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/questions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/inquiries/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/inquiries/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/inquiries/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
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
        CorsConfiguration configuration = getCorsConfiguration();

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie", "Access-Control-Allow-Credentials"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:3000");
        configuration.addAllowedOriginPattern("http://localhost:8080");
        configuration.addAllowedOriginPattern("http://www.rideon.kro.kr");
        configuration.addAllowedOriginPattern("https://www.rideon.kro.kr");
        configuration.addAllowedOriginPattern("http://211.197.178.97:11200");
        configuration.addAllowedOriginPattern("https://211.197.178.97:11200");
        return configuration;
    }
}
