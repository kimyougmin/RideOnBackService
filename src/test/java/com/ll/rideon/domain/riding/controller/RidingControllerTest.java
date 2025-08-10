package com.ll.rideon.domain.riding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.domain.riding.dto.RidingSessionCreateRequestDto;
import com.ll.rideon.domain.riding.dto.RidingSessionResponseDto;
import com.ll.rideon.domain.riding.service.RidingService;
import com.ll.rideon.global.security.custom.CustomUserDetails;
import com.ll.rideon.domain.users.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class RidingControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private RidingService ridingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // SecurityContext 설정
        Users user = Users.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .build();
        
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createRidingSession_Success() throws Exception {
        // Given
        RidingSessionCreateRequestDto requestDto = new RidingSessionCreateRequestDto();
        RidingSessionResponseDto responseDto = RidingSessionResponseDto.builder()
                .id(1L)
                .userId(1L)
                .build();
        
        when(ridingService.createRidingSession(eq(1L), any(RidingSessionCreateRequestDto.class)))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/riding/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "testuser")
    void endRidingSession_Success() throws Exception {
        // Given
        Long sessionId = 1L;

        // When & Then
        mockMvc.perform(put("/api/riding/sessions/{sessionId}/end", sessionId))
                .andExpect(status().isOk());
    }
}
