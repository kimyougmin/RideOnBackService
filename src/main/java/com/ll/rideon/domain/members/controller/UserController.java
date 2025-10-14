package com.ll.rideon.domain.members.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ll.rideon.domain.members.dto.*;
import com.ll.rideon.domain.members.service.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Tag(name = "유저 인증", description = "로그인 및 회원가입 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "회원가입 성공 반환")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @ApiResponse(responseCode = "401", description = "잘못된 자격 증명", content = @Content)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto dto) {
        log.info("회원가입 시도 - 이메일: {}", dto.getEmail());
        
        try {
            userService.register(dto);
            log.info("회원가입 성공 - 이메일: {}", dto.getEmail());
            
            Map<String, String> response = new HashMap<>();
            response.put("res", "회원가입 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회원가입 실패 - 이메일: {}, 오류: {}", dto.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰과 사용자 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "401", description = "잘못된 자격 증명", content = @Content)
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        log.info("로그인 시도 - 이메일: {}", dto.getEmail());
        
        try {
            UserLoginResponseDto response = userService.login(dto);
            log.info("로그인 성공 - 이메일: {}", dto.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("로그인 실패 - 이메일: {}, 오류: {}", dto.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "프로필 정보 업데이트", description = "로그인한 사용자의 프로필 정보를 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 정보 업데이트 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(@RequestBody UserProfileUpdateDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        
        log.info("프로필 정보 업데이트 시도 - 사용자 ID: {}", userId);
        
        try {
            UserProfileResponseDto response = userService.updateProfile(userId, dto);
            log.info("프로필 정보 업데이트 성공 - 사용자 ID: {}", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("프로필 정보 업데이트 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "프로필 이미지 업데이트 (URL)", description = "로그인한 사용자의 프로필 이미지 URL을 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 이미지 업데이트 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    @PutMapping("/profile/image/url")
    public ResponseEntity<UserProfileImageUpdateDto> updateProfileImageUrl(@RequestParam("imageUrl") String imageUrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        
        log.info("프로필 이미지 URL 업데이트 시도 - 사용자 ID: {}", userId);
        
        try {
            UserProfileImageUpdateDto response = userService.updateProfileImage(userId, imageUrl);
            log.info("프로필 이미지 URL 업데이트 성공 - 사용자 ID: {}", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("프로필 이미지 URL 업데이트 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "프로필 이미지 업데이트 (파일 업로드)", description = "로그인한 사용자의 프로필 이미지 파일을 업로드하여 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 이미지 업데이트 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 파일 형식", content = @Content)
    @PostMapping("/profile/image/upload")
    public ResponseEntity<UserProfileImageUpdateDto> updateProfileImageFile(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        
        log.info("프로필 이미지 파일 업로드 시도 - 사용자 ID: {}, 파일명: {}", userId, file.getOriginalFilename());
        
        // 파일 업로드 기능이 제거되었으므로 에러 응답
        log.error("파일 업로드 기능이 제거되었습니다 - 사용자 ID: {}", userId);
        return ResponseEntity.badRequest().body(
            UserProfileImageUpdateDto.builder()
                .message("파일 업로드 기능이 현재 지원되지 않습니다.")
                .build()
        );
    }
}