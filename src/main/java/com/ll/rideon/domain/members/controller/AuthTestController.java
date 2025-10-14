package com.ll.rideon.domain.members.controller;

import com.ll.rideon.domain.members.entity.Members;
import com.ll.rideon.domain.members.repository.UserRepository;
import com.ll.rideon.global.security.custom.CustomUserDetails;
import com.ll.rideon.global.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class AuthTestController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/create-test-user")
    public ResponseEntity<Map<String, Object>> createTestUser() {
        // 테스트용 사용자 생성
        Members testMember = Members.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .name("테스트 사용자")
                .build();

        Members savedMember = userRepository.save(testMember);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "테스트 사용자가 생성되었습니다.");
        response.put("userId", savedMember.getId());
        response.put("email", savedMember.getEmail());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            CustomUserDetails userDetails = SecurityUtil.getCurrentUser();

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("email", userDetails.getUsername());
            response.put("name", userDetails.getMembers().getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "인증되지 않은 사용자입니다.");
            response.put("message", e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }
} 