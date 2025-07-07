package com.ll.rideon.domain.users.controller;

import com.ll.rideon.domain.users.dto.UserLoginRequestDto;
import com.ll.rideon.domain.users.dto.UserLoginResponseDto;
import com.ll.rideon.domain.users.dto.UserRegisterDto;
import com.ll.rideon.domain.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Tag(name = "유저 인증", description = "로그인 및 회원가입 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "회원가입 성공 반환")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @ApiResponse(responseCode = "401", description = "잘못된 자격 증명", content = @Content)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto dto) {
        userService.register(dto);
        Map<String, String> response = new HashMap<>();
        response.put("res", "회원가입 성공");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰과 사용자 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "401", description = "잘못된 자격 증명", content = @Content)
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        UserLoginResponseDto response = userService.login(dto);
        return ResponseEntity.ok(response);
    }
}