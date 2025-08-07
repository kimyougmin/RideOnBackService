package com.ll.rideon.domain.users.controller;

import com.ll.rideon.domain.users.entity.Users;
import com.ll.rideon.domain.users.repository.UserRepository;
import com.ll.rideon.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String name = request.get("name");

        // 이미 존재하는 사용자인지 확인
        if (userRepository.findByEmail(email).isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "이미 존재하는 이메일입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 새 사용자 생성
        Users user = Users.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();

        Users savedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었습니다.");
        response.put("userId", savedUser.getId());
        response.put("email", savedUser.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인이 성공했습니다.");
            response.put("userId", userDetails.getUsers().getId());
            response.put("email", userDetails.getUsername());
            response.put("name", userDetails.getUsers().getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "로그인에 실패했습니다.");
            response.put("message", "이메일 또는 비밀번호를 확인해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 