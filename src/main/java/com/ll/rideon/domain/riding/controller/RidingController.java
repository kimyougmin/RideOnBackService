package com.ll.rideon.domain.riding.controller;

import com.ll.rideon.domain.riding.dto.LocationUpdateRequestDto;
import com.ll.rideon.domain.riding.dto.NetworkRecommendation;
import com.ll.rideon.domain.riding.dto.NetworkStatusRequestDto;
import com.ll.rideon.domain.riding.dto.RidingSessionCreateRequestDto;
import com.ll.rideon.domain.riding.dto.RidingSessionResponseDto;
import com.ll.rideon.domain.riding.entity.RidingLocation;
import com.ll.rideon.domain.riding.service.RidingService;
import com.ll.rideon.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/riding")
@RequiredArgsConstructor
@Tag(name = "🚴‍♂️ 라이딩 추적", description = "실시간 라이딩 세션 관리 및 위치 추적 API")
public class RidingController {

    private final RidingService ridingService;

    @PostMapping("/sessions")
    @Operation(
            summary = "🚴‍♂️ 라이딩 세션 시작",
            description = """
                    새로운 라이딩 세션을 시작합니다.
                    
                    ## 📋 기능 설명
                    - 새로운 라이딩 세션을 생성하고 활성 상태로 시작합니다
                    - 세션 ID가 생성되어 이후 위치 추적에 사용됩니다
                    - 로그인한 사용자만 세션을 생성할 수 있습니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```json
                    {}
                    ```
                    (현재는 빈 객체로 요청, 향후 시작 위치 등 추가 예정)
                    
                    ## 🔄 다음 단계
                    1. 세션 생성 후 위치 정보 업데이트 API 호출
                    2. 네트워크 상태 업데이트 API 호출
                    3. 라이딩 완료 시 세션 종료 API 호출
                    
                    ## ⚠️ 주의사항
                    - 한 사용자당 하나의 활성 세션만 가질 수 있습니다
                    - 기존 활성 세션이 있다면 자동으로 종료됩니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "라이딩 세션 생성 성공",
                    content = @Content(schema = @Schema(implementation = RidingSessionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (로그인이 필요합니다)")
    })
    public ResponseEntity<RidingSessionResponseDto> createRidingSession(
            @Parameter(description = "라이딩 세션 생성 정보 (현재는 빈 객체)", required = true)
            @Validated @RequestBody RidingSessionCreateRequestDto requestDto) {
        
        Long userId = SecurityUtil.getCurrentUserId();
        RidingSessionResponseDto responseDto = ridingService.createRidingSession(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/sessions/{sessionId}/location")
    @Operation(
            summary = "📍 위치 정보 업데이트",
            description = """
                    라이딩 중인 세션의 현재 위치 정보를 업데이트합니다.
                    
                    ## 📋 기능 설명
                    - 실시간 위치 정보를 데이터베이스에 저장합니다
                    - GPS 좌표, 속도, 고도, 정확도 등 상세 정보를 기록합니다
                    - 네트워크 상태와 배터리 레벨도 함께 기록됩니다
                    
                    ## 📍 위치 정보
                    - 위도/경도: GPS 좌표 (필수)
                    - 속도: 현재 이동 속도 (km/h)
                    - 고도: 현재 고도 (미터)
                    - 정확도: GPS 정확도 (미터)
                    - 방향: 이동 방향 (도)
                    
                    ## 📝 사용 예시
                    ```json
                    {
                      "latitude": 37.5665,
                      "longitude": 126.9780,
                      "speedKmh": 25.5,
                      "altitude": 45.2,
                      "accuracy": 5.0,
                      "heading": 180.0,
                      "recordedAt": "2024-01-01T12:00:00",
                      "networkQuality": "GOOD",
                      "batteryLevel": 85,
                      "isOfflineSync": false
                    }
                    ```
                    
                    ## 🔄 활용 방법
                    - 모바일 앱에서 주기적으로 호출 (예: 5초마다)
                    - 네트워크 불안정 시 오프라인 저장 후 동기화
                    - 배터리 레벨이 낮을 때 업데이트 빈도 조절
                    
                    ## ⚠️ 주의사항
                    - 위도/경도는 필수 입력값입니다
                    - 네트워크 품질은 미리 정의된 값만 사용 가능합니다
                    - 오프라인 동기화 시 isOfflineSync를 true로 설정
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치 정보 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (위도/경도 누락 등)"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (로그인이 필요합니다)"),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음 (존재하지 않는 세션 ID)")
    })
    public ResponseEntity<Void> updateLocation(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId,
            @Parameter(description = "위치 정보 (위도, 경도, 속도, 네트워크 상태 등)", required = true)
            @Validated @RequestBody LocationUpdateRequestDto requestDto) {
        
        ridingService.updateLocation(sessionId, requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sessions/{sessionId}/network")
    @Operation(
            summary = "📶 네트워크 상태 업데이트",
            description = "라이딩 중인 세션의 네트워크 상태 정보를 업데이트합니다. 로그인이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "네트워크 상태 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음")
    })
    public ResponseEntity<Void> updateNetworkStatus(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId,
            @Parameter(description = "네트워크 상태 정보", required = true)
            @Validated @RequestBody NetworkStatusRequestDto requestDto) {
        
        ridingService.updateNetworkStatus(sessionId, requestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/sessions/{sessionId}/end")
    @Operation(
            summary = "🏁 라이딩 세션 종료",
            description = "라이딩 세션을 종료합니다. 로그인이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라이딩 세션 종료 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음")
    })
    public ResponseEntity<Void> endRidingSession(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {
        ridingService.endRidingSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/sessions/{sessionId}/pause")
    @Operation(
            summary = "⏸️ 라이딩 세션 일시정지",
            description = """
                    라이딩 세션을 일시정지합니다.
                    
                    ## 📋 기능 설명
                    - 현재 진행 중인 라이딩 세션을 일시정지 상태로 변경합니다
                    - 일시정지 중에는 위치 추적이 중단됩니다
                    - 필요시 재개할 수 있습니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```
                    PUT /api/riding/sessions/1/pause
                    Authorization: Bearer your-jwt-token
                    ```
                    
                    ## ⚠️ 주의사항
                    - 존재하지 않는 세션 ID로 요청 시 404 에러가 발생합니다
                    - 이미 종료된 세션은 일시정지할 수 없습니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라이딩 세션 일시정지 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음")
    })
    public ResponseEntity<Void> pauseRidingSession(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {
        ridingService.pauseRidingSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/sessions/{sessionId}/resume")
    @Operation(
            summary = "▶️ 라이딩 세션 재개",
            description = """
                    일시정지된 라이딩 세션을 재개합니다.
                    
                    ## 📋 기능 설명
                    - 일시정지된 라이딩 세션을 다시 활성 상태로 변경합니다
                    - 재개 후 위치 추적이 다시 시작됩니다
                    - 기존 데이터는 그대로 유지됩니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```
                    PUT /api/riding/sessions/1/resume
                    Authorization: Bearer your-jwt-token
                    ```
                    
                    ## ⚠️ 주의사항
                    - 존재하지 않는 세션 ID로 요청 시 404 에러가 발생합니다
                    - 이미 종료된 세션은 재개할 수 없습니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라이딩 세션 재개 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음")
    })
    public ResponseEntity<Void> resumeRidingSession(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {
        ridingService.resumeRidingSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(
            summary = "📖 라이딩 세션 상세 조회",
            description = """
                    특정 라이딩 세션의 상세 정보를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 라이딩 세션 ID로 특정 세션의 상세 정보를 조회합니다
                    - 세션 상태, 시작/종료 시간, 거리, 속도 등 모든 정보를 확인할 수 있습니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/riding/sessions/1
                    ```
                    
                    ## 🔍 응답 정보
                    - 세션 기본 정보 (ID, 상태, 시작/종료 시간)
                    - 라이딩 통계 (총 거리, 평균/최대 속도, 칼로리 소모)
                    - 마지막 위치 정보
                    - 네트워크 품질 정보
                    
                    ## ⚠️ 주의사항
                    - 존재하지 않는 세션 ID로 요청 시 404 에러가 발생합니다
                    - 종료된 세션의 경우 완료된 통계 정보를 제공합니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라이딩 세션 조회 성공",
                    content = @Content(schema = @Schema(implementation = RidingSessionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음")
    })
    public ResponseEntity<RidingSessionResponseDto> getRidingSession(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {
        RidingSessionResponseDto responseDto = ridingService.getRidingSession(sessionId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/sessions")
    @Operation(
            summary = "📋 내 라이딩 세션 목록",
            description = """
                    사용자의 라이딩 세션 목록을 페이지네이션으로 조회합니다.
                    
                    ## 📋 기능 설명
                    - 현재 로그인한 사용자의 모든 라이딩 세션을 조회합니다
                    - 세션 시작 시간 기준으로 최신순으로 정렬됩니다
                    - 로그인이 필요합니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📄 페이지네이션
                    - 기본 페이지 크기: 10개
                    - 페이지 번호: 0부터 시작
                    - 정렬: 시작 시간 기준 내림차순
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/riding/sessions?page=0&size=10
                    Authorization: Bearer your-jwt-token
                    ```
                    
                    ## 🔍 응답 정보
                    - 라이딩 세션 목록 (상태, 시작/종료 시간, 거리, 속도)
                    - 페이지 정보 (현재 페이지, 전체 페이지 수, 전체 세션 수)
                    
                    ## ⚠️ 주의사항
                    - 로그인하지 않은 상태에서는 401 에러가 발생합니다
                    - 페이지 번호는 0부터 시작합니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라이딩 세션 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = RidingSessionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (로그인이 필요합니다)")
    })
    public ResponseEntity<Page<RidingSessionResponseDto>> getUserRidingSessions(
            @Parameter(description = "페이지 정보 (page: 페이지 번호, size: 페이지 크기, 기본값: size=10)")
            @PageableDefault(size = 10) Pageable pageable) {
        
        Long userId = SecurityUtil.getCurrentUserId();
        Page<RidingSessionResponseDto> responseDto = ridingService.getUserRidingSessions(userId, pageable);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/sessions/{sessionId}/locations")
    @Operation(
            summary = "📍 라이딩 경로 조회",
            description = """
                    특정 라이딩 세션의 모든 위치 정보를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 라이딩 세션 동안 기록된 모든 위치 정보를 조회합니다
                    - GPS 좌표, 속도, 고도, 시간 등 상세한 경로 정보를 제공합니다
                    - 지도에 경로를 표시하는 데 사용할 수 있습니다
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/riding/sessions/1/locations
                    ```
                    
                    ## 🔍 응답 정보
                    - 모든 위치 정보 (위도/경도, 속도, 고도, 정확도, 방향)
                    - 각 위치의 기록 시간
                    - 네트워크 품질 및 배터리 레벨
                    
                    ## ⚠️ 주의사항
                    - 존재하지 않는 세션 ID로 요청 시 404 에러가 발생합니다
                    - 위치 데이터가 많은 경우 응답 시간이 오래 걸릴 수 있습니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라이딩 경로 조회 성공",
                    content = @Content(schema = @Schema(implementation = RidingLocation.class))),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음")
    })
    public ResponseEntity<List<RidingLocation>> getRidingLocations(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {
        List<RidingLocation> locations = ridingService.getRidingLocations(sessionId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/sessions/active")
    @Operation(
            summary = "🔄 현재 활성 세션 조회",
            description = """
                    현재 로그인한 사용자의 활성 라이딩 세션을 조회합니다.
                    
                    ## 📋 기능 설명
                    - 현재 진행 중인 라이딩 세션의 정보를 조회합니다
                    - 활성 세션이 없으면 null을 반환합니다
                    - 로그인이 필요합니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/riding/sessions/active
                    Authorization: Bearer your-jwt-token
                    ```
                    
                    ## 🔍 응답 정보
                    - 활성 세션 정보 (시작 시간, 현재 상태, 마지막 위치)
                    - 활성 세션이 없으면 null 반환
                    
                    ## ⚠️ 주의사항
                    - 로그인하지 않은 상태에서는 401 에러가 발생합니다
                    - 한 사용자당 하나의 활성 세션만 가질 수 있습니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성 세션 조회 성공",
                    content = @Content(schema = @Schema(implementation = RidingSessionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (로그인이 필요합니다)")
    })
    public ResponseEntity<RidingSessionResponseDto> getActiveSession() {
        Long userId = SecurityUtil.getCurrentUserId();
        RidingSessionResponseDto responseDto = ridingService.getActiveSession(userId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/sessions/{sessionId}/sync-offline")
    @Operation(
            summary = "🔄 오프라인 데이터 동기화",
            description = """
                    오프라인으로 저장된 라이딩 데이터를 서버와 동기화합니다.
                    
                    ## 📋 기능 설명
                    - 네트워크 불안정으로 오프라인에 저장된 위치 데이터를 서버로 전송합니다
                    - 여러 개의 위치 데이터를 한 번에 동기화할 수 있습니다
                    - 로그인이 필요합니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```
                    POST /api/riding/sessions/1/sync-offline
                    Authorization: Bearer your-jwt-token
                    ```
                    
                    ## 🔄 동기화 과정
                    1. 오프라인에 저장된 위치 데이터 수집
                    2. 네트워크 연결 확인
                    3. 서버로 데이터 전송
                    4. 동기화 완료 후 오프라인 데이터 삭제
                    
                    ## ⚠️ 주의사항
                    - 로그인하지 않은 상태에서는 401 에러가 발생합니다
                    - 존재하지 않는 세션 ID로 요청 시 404 에러가 발생합니다
                    - 대용량 데이터 동기화 시 시간이 오래 걸릴 수 있습니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "오프라인 데이터 동기화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "라이딩 세션을 찾을 수 없음")
    })
    public ResponseEntity<Void> syncOfflineData(
            @Parameter(description = "라이딩 세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {
        ridingService.syncOfflineData(sessionId);
        return ResponseEntity.ok().build();
    }
} 