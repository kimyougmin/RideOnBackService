package com.ll.rideon.domain.users.controller;

import com.ll.rideon.domain.users.dto.UserLoginRequestDto;
import com.ll.rideon.domain.users.dto.UserLoginResponseDto;
import com.ll.rideon.domain.users.dto.UserRegisterDto;
import com.ll.rideon.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto dto) {
        userService.register(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto dto) {
        String token = userService.login(dto);
        return ResponseEntity.ok(new UserLoginResponseDto(token));
    }
}