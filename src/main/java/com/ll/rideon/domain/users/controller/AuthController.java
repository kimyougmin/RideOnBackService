package com.ll.rideon.domain.users.controller;

import com.ll.rideon.domain.users.entity.Users;
import com.ll.rideon.domain.users.repository.UserRepository;
import com.ll.rideon.global.security.custom.CustomUserDetails;
import com.ll.rideon.global.security.jwt.JwtTokenProvider;
import com.ll.rideon.global.security.jwt.service.TokenManagementService;
import com.ll.rideon.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "🔐 인증", description = "사용자 인증 관련 API (로그인, 회원가입, 토큰 관리)")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenManagementService tokenManagementService;

    @PostMapping("/register")
    @Operation(
            summary = "📝 회원가입",
            description = """
                    새로운 사용자를 등록합니다.
                    
                    ## 📋 기능 설명
                    - 이메일, 비밀번호, 이름을 사용하여 새로운 계정을 생성합니다
                    - 비밀번호는 암호화되어 저장됩니다
                    - 중복된 이메일은 사용할 수 없습니다
                    
                    ## 📝 사용 예시
                    ```json
                    {
                      "email": "user@example.com",
                      "password": "password123",
                      "name": "홍길동"
                    }
                    ```
                    
                    ## 🔍 응답 정보
                    - 성공: 회원가입 완료 메시지와 사용자 정보
                    - 실패: 중복 이메일 오류 메시지
                    
                    ## ⚠️ 주의사항
                    - 이메일은 고유해야 합니다
                    - 비밀번호는 최소 6자 이상이어야 합니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(example = """
                            {
                              "message": "회원가입이 완료되었습니다.",
                              "userId": 1,
                              "email": "user@example.com"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "중복된 이메일 또는 잘못된 요청 데이터")
    })
    public ResponseEntity<Map<String, Object>> register(
            @Parameter(description = "회원가입 정보 (이메일, 비밀번호, 이름)", required = true)
            @RequestBody Map<String, String> request) {
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
    @Operation(
            summary = "🔑 로그인",
            description = """
                    사용자 로그인을 수행하고 JWT 토큰을 발급합니다.
                    
                    ## 📋 기능 설명
                    - 이메일과 비밀번호로 사용자 인증을 수행합니다
                    - 인증 성공 시 액세스 토큰과 리프레시 토큰을 발급합니다
                    - 액세스 토큰은 1시간, 리프레시 토큰은 7일간 유효합니다
                    
                    ## 🔐 토큰 정보
                    - **액세스 토큰**: API 호출 시 사용 (1시간 유효)
                    - **리프레시 토큰**: 액세스 토큰 갱신 시 사용 (7일 유효)
                    
                    ## 📝 사용 예시
                    ```json
                    {
                      "email": "user@example.com",
                      "password": "password123"
                    }
                    ```
                    
                    ## 🔍 응답 정보
                    - 성공: 사용자 정보와 JWT 토큰들
                    - 실패: 인증 실패 메시지
                    
                    ## ⚠️ 주의사항
                    - 액세스 토큰은 Authorization 헤더에 Bearer 형식으로 포함해야 합니다
                    - 리프레시 토큰은 안전한 곳에 보관하고 액세스 토큰 갱신 시에만 사용해야 합니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(example = """
                            {
                              "message": "로그인이 성공했습니다.",
                              "userId": 1,
                              "email": "user@example.com",
                              "name": "홍길동",
                              "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                              "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                              "tokenType": "Bearer",
                              "expiresIn": 3600
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "로그인 실패 (잘못된 이메일 또는 비밀번호)")
    })
    public ResponseEntity<Map<String, Object>> login(
            @Parameter(description = "로그인 정보 (이메일, 비밀번호)", required = true)
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Users user = userDetails.getUsers();

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getId(), user.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getId(), user.getEmail());

            // 리프레시 토큰 저장
            tokenManagementService.saveRefreshToken(user.getEmail(), refreshToken);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인이 성공했습니다.");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 3600); // 1시간 (초 단위)

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "로그인에 실패했습니다.");
            response.put("message", "이메일 또는 비밀번호를 확인해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "🔄 토큰 갱신",
            description = """
                    리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
                    
                    ## 📋 기능 설명
                    - 만료된 액세스 토큰을 리프레시 토큰으로 갱신합니다
                    - 리프레시 토큰이 유효한 경우에만 새로운 액세스 토큰을 발급합니다
                    - 새로운 액세스 토큰은 1시간간 유효합니다
                    
                    ## 🔐 토큰 갱신 과정
                    1. 리프레시 토큰 유효성 검증
                    2. 사용자 정보 추출
                    3. 새로운 액세스 토큰 생성
                    4. 응답으로 새 토큰 반환
                    
                    ## 📝 사용 예시
                    ```json
                    {
                      "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                    }
                    ```
                    
                    ## 🔍 응답 정보
                    - 성공: 새로운 액세스 토큰
                    - 실패: 토큰 갱신 실패 메시지
                    
                    ## ⚠️ 주의사항
                    - 리프레시 토큰이 만료되면 다시 로그인해야 합니다
                    - 리프레시 토큰은 안전하게 보관해야 합니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(example = """
                            {
                              "message": "토큰이 성공적으로 갱신되었습니다.",
                              "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                              "tokenType": "Bearer",
                              "expiresIn": 3600
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "토큰 갱신 실패 (유효하지 않은 리프레시 토큰)"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Map<String, Object>> refreshToken(
            @Parameter(description = "리프레시 토큰", required = true)
            @RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "리프레시 토큰이 필요합니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = tokenManagementService.refreshAccessToken(refreshToken);

        if (newAccessToken == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "토큰 갱신에 실패했습니다.");
            response.put("message", "리프레시 토큰이 유효하지 않거나 만료되었습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "토큰이 성공적으로 갱신되었습니다.");
        response.put("accessToken", newAccessToken);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 3600); // 1시간 (초 단위)

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "🚪 로그아웃",
            description = """
                    사용자 로그아웃을 수행하고 토큰을 무효화합니다.
                    
                    ## 📋 기능 설명
                    - 현재 사용자의 리프레시 토큰을 삭제합니다
                    - 액세스 토큰은 만료될 때까지 유효하지만, 리프레시 토큰으로 갱신할 수 없습니다
                    - 보안을 위해 클라이언트에서도 토큰을 삭제해야 합니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```
                    POST /api/auth/logout
                    Authorization: Bearer your-access-token
                    ```
                    
                    ## 🔍 응답 정보
                    - 성공: 로그아웃 완료 메시지
                    
                    ## ⚠️ 주의사항
                    - 로그아웃 후에는 새로운 로그인이 필요합니다
                    - 클라이언트에서 저장된 토큰도 삭제해야 합니다
                    """,
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(example = """
                            {
                              "message": "로그아웃이 완료되었습니다."
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<Map<String, Object>> logout() {
        try {
            // 현재 사용자 이메일 가져오기
            String currentUserEmail = SecurityUtil.getCurrentUserEmail();
            
            // 리프레시 토큰 삭제
            tokenManagementService.logout(currentUserEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그아웃이 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "로그아웃 처리 중 오류가 발생했습니다.");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 