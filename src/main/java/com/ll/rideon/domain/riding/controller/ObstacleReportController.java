package com.ll.rideon.domain.riding.controller;

import com.ll.rideon.domain.riding.dto.ObstacleReportRequestDto;
import com.ll.rideon.domain.riding.dto.ObstacleReportResponseDto;
import com.ll.rideon.domain.riding.dto.NearbyObstaclesRequestDto;
import com.ll.rideon.domain.riding.entity.ObstacleReport;
import com.ll.rideon.domain.riding.service.ObstacleReportService;
import com.ll.rideon.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/obstacles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "🚧 장애물 신고", description = "라이딩 중 발견한 장애물을 신고하고 조회하는 API")
public class ObstacleReportController {

    private final ObstacleReportService obstacleReportService;
    private final SecurityUtil securityUtil;

    @PostMapping("/report")
    @Operation(
            summary = "🚧 장애물 신고",
            description = """
                    라이딩 중 발견한 장애물을 신고합니다.
                    
                    ## 📋 기능 설명
                    - 라이딩 중 발견한 장애물의 위치와 정보를 신고합니다
                    - 장애물의 종류, 위치(위도/경도), 설명, 이미지를 포함합니다
                    - 신고된 장애물은 지도에 마커로 표시됩니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```json
                    {
                      "latitude": 37.5665,
                      "longitude": 126.9780,
                      "reportType": "OBSTACLE",
                      "description": "강남역 앞에 큰 바위가 길을 막고 있습니다",
                      "image": "https://example.com/obstacle-photo.jpg"
                    }
                    ```
                    
                    ## 🏷️ 장애물 종류
                    - OBSTACLE: 장애물 (바위, 쓰러진 나무 등)
                    - ROAD_DAMAGE: 도로 손상 (구멍, 균열 등)
                    - CONSTRUCTION: 공사 (도로 공사, 건설 작업 등)
                    - SLIPPERY: 미끄러운 도로 (기름, 얼음 등)
                    - ETC: 기타
                    
                    ## ⚠️ 주의사항
                    - 위도/경도는 정확한 위치를 입력해야 합니다
                    - 설명은 1000자 이하여야 합니다
                    - 이미지 URL은 500자 이하여야 합니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장애물 신고 성공",
                    content = @Content(schema = @Schema(implementation = ObstacleReportResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (로그인이 필요합니다)")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ObstacleReportResponseDto> reportObstacle(
            @Parameter(description = "장애물 신고 정보 (위치, 종류, 설명, 이미지)", required = true)
            @Valid @RequestBody ObstacleReportRequestDto requestDto) {
        
        log.info("장애물 신고 시도 - 사용자 ID: {}, 위치: ({}, {}), 종류: {}", 
                securityUtil.getCurrentUserId(), requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getReportType());
        
        try {
            Long userId = securityUtil.getCurrentUserId();
            ObstacleReportResponseDto response = obstacleReportService.createObstacleReport(userId, requestDto);
            
            log.info("장애물 신고 성공 - 신고 ID: {}, 사용자 ID: {}, 위치: ({}, {})", 
                    response.getId(), userId, requestDto.getLatitude(), requestDto.getLongitude());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("장애물 신고 실패 - 사용자 ID: {}, 위치: ({}, {}), 오류: {}", 
                    securityUtil.getCurrentUserId(), requestDto.getLatitude(), requestDto.getLongitude(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/nearby")
    @Operation(
            summary = "📍 주변 장애물 조회",
            description = """
                    현재 위치 주변의 장애물을 조회합니다.
                    
                    ## 📋 기능 설명
                    - 지정된 위치 주변의 장애물 신고 목록을 조회합니다
                    - 반경 내의 모든 장애물을 거리순으로 정렬하여 반환합니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📍 위치 정보
                    - 위도(latitude): -90 ~ 90 사이의 값
                    - 경도(longitude): -180 ~ 180 사이의 값
                    - 반경(radius): 조회할 반경 (킬로미터 단위)
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/nearby?latitude=37.5665&longitude=126.9780&radius=1.0
                    ```
                    
                    ## 🔍 응답 정보
                    - 장애물 목록 (위치, 종류, 설명, 상태, 신고일시)
                    - 거리순으로 정렬된 결과
                    
                    ## ⚠️ 주의사항
                    - 반경은 0.1km ~ 10km 사이로 설정하는 것을 권장합니다
                    - 너무 큰 반경은 성능에 영향을 줄 수 있습니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주변 장애물 조회 성공",
                    content = @Content(schema = @Schema(implementation = ObstacleReportResponseDto.class)))
    })
    public ResponseEntity<List<ObstacleReportResponseDto>> getNearbyObstacles(
            @Parameter(description = "주변 장애물 조회 조건 (위치, 반경)", required = true)
            @Valid @ModelAttribute NearbyObstaclesRequestDto requestDto) {
        
        log.info("주변 장애물 조회 시도 - 위치: ({}, {}), 반경: {}km", 
                requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getRadius());
        
        try {
            List<ObstacleReportResponseDto> obstacles = obstacleReportService.getNearbyObstacles(requestDto);
            log.info("주변 장애물 조회 성공 - 위치: ({}, {}), 반경: {}km, 조회된 장애물 수: {}", 
                    requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getRadius(), obstacles.size());
            return ResponseEntity.ok(obstacles);
        } catch (Exception e) {
            log.error("주변 장애물 조회 실패 - 위치: ({}, {}), 반경: {}km, 오류: {}", 
                    requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getRadius(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/route")
    @Operation(
            summary = "🛣️ 경로 상 장애물 조회",
            description = """
                    특정 경로 상의 장애물을 조회합니다.
                    
                    ## 📋 기능 설명
                    - 시작점과 끝점을 연결하는 경로 상의 장애물을 조회합니다
                    - 경로에서 일정 거리 내의 장애물을 모두 포함합니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 🛣️ 경로 정보
                    - 시작점: 위도/경도 좌표
                    - 끝점: 위도/경도 좌표
                    - 경로 상 100m 이내의 장애물을 포함
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/route?startLat=37.5665&startLng=126.9780&endLat=37.5572&endLng=126.9254
                    ```
                    
                    ## 🔍 응답 정보
                    - 경로 상 장애물 목록
                    - 장애물의 위치와 상세 정보
                    
                    ## ⚠️ 주의사항
                    - 시작점과 끝점은 유효한 좌표여야 합니다
                    - 경로가 너무 길면 성능에 영향을 줄 수 있습니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "경로 상 장애물 조회 성공",
                    content = @Content(schema = @Schema(implementation = ObstacleReportResponseDto.class)))
    })
    public ResponseEntity<List<ObstacleReportResponseDto>> getObstaclesInRoute(
            @Parameter(description = "시작점 위도", required = true, example = "37.5665")
            @RequestParam Double startLat,
            @Parameter(description = "시작점 경도", required = true, example = "126.9780")
            @RequestParam Double startLng,
            @Parameter(description = "끝점 위도", required = true, example = "37.5572")
            @RequestParam Double endLat,
            @Parameter(description = "끝점 경도", required = true, example = "126.9254")
            @RequestParam Double endLng) {
        
        log.info("경로 상 장애물 조회 시도 - 시작점: ({}, {}), 끝점: ({}, {})", startLat, startLng, endLat, endLng);
        
        try {
            List<ObstacleReportResponseDto> obstacles = obstacleReportService.getObstaclesInRoute(
                    startLat, startLng, endLat, endLng);
            log.info("경로 상 장애물 조회 성공 - 시작점: ({}, {}), 끝점: ({}, {}), 조회된 장애물 수: {}", 
                    startLat, startLng, endLat, endLng, obstacles.size());
            return ResponseEntity.ok(obstacles);
        } catch (Exception e) {
            log.error("경로 상 장애물 조회 실패 - 시작점: ({}, {}), 끝점: ({}, {}), 오류: {}", 
                    startLat, startLng, endLat, endLng, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/my-reports")
    @Operation(
            summary = "📋 내 장애물 신고 목록",
            description = """
                    사용자가 신고한 장애물 목록을 조회합니다.
                    
                    ## 📋 기능 설명
                    - 현재 로그인한 사용자가 신고한 모든 장애물 목록을 조회합니다
                    - 신고일시 기준으로 최신순으로 정렬됩니다
                    - 로그인이 필요합니다
                    
                    ## 🔐 인증 요구사항
                    - 로그인이 필요합니다 (JWT 토큰 필요)
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/my-reports
                    Authorization: Bearer your-jwt-token
                    ```
                    
                    ## 🔍 응답 정보
                    - 내가 신고한 장애물 목록
                    - 각 장애물의 상세 정보와 현재 상태
                    
                    ## ⚠️ 주의사항
                    - 로그인하지 않은 상태에서는 401 에러가 발생합니다
                    - 신고한 장애물이 없으면 빈 배열이 반환됩니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 장애물 신고 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ObstacleReportResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (로그인이 필요합니다)")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ObstacleReportResponseDto>> getMyObstacleReports() {
        
        Long userId = securityUtil.getCurrentUserId();
        List<ObstacleReportResponseDto> reports = obstacleReportService.getUserObstacleReports(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{reportId}")
    @Operation(
            summary = "📖 장애물 신고 상세 조회",
            description = """
                    특정 장애물 신고의 상세 정보를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 장애물 신고 ID로 특정 신고의 상세 정보를 조회합니다
                    - 신고자 정보, 위치, 설명, 이미지 등 모든 정보를 확인할 수 있습니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/1
                    ```
                    
                    ## 🔍 응답 정보
                    - 장애물 신고 상세 정보
                    - 신고자 정보 (익명 처리)
                    - 위치, 종류, 설명, 이미지
                    - 신고 상태 및 신고일시
                    
                    ## ⚠️ 주의사항
                    - 존재하지 않는 신고 ID로 요청 시 404 에러가 발생합니다
                    - 신고자 정보는 개인정보 보호를 위해 제한적으로 제공됩니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장애물 신고 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = ObstacleReportResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "장애물 신고를 찾을 수 없음")
    })
    public ResponseEntity<ObstacleReportResponseDto> getObstacleReport(
            @Parameter(description = "장애물 신고 ID", required = true, example = "1")
            @PathVariable Long reportId) {
        
        ObstacleReportResponseDto report = obstacleReportService.getObstacleReport(reportId);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/{reportId}/status")
    @Operation(
            summary = "🔄 장애물 신고 상태 업데이트",
            description = """
                    장애물 신고의 상태를 업데이트합니다.
                    
                    ## 📋 기능 설명
                    - 장애물 신고의 처리 상태를 변경합니다
                    - 관리자만 상태를 변경할 수 있습니다
                    - 로그인이 필요합니다
                    
                    ## 🔐 인증 요구사항
                    - 관리자 권한이 필요합니다
                    - Authorization 헤더에 Bearer 토큰을 포함해야 합니다
                    
                    ## 📊 신고 상태
                    - UNCONFIRMED: 미확인 (신고 접수됨)
                    - CONFIRMED: 확인됨 (관리자가 확인함)
                    - RESOLVED: 해결됨 (문제가 해결됨)
                    
                    ## 📝 사용 예시
                    ```
                    PUT /api/v1/obstacles/1/status?status=CONFIRMED
                    Authorization: Bearer admin-jwt-token
                    ```
                    
                    ## ⚠️ 주의사항
                    - 관리자 권한이 없으면 403 에러가 발생합니다
                    - 존재하지 않는 신고 ID로 요청 시 404 에러가 발생합니다
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = ObstacleReportResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자만 가능)"),
            @ApiResponse(responseCode = "404", description = "장애물 신고를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObstacleReportResponseDto> updateObstacleReportStatus(
            @Parameter(description = "장애물 신고 ID", required = true, example = "1")
            @PathVariable Long reportId,
            @Parameter(description = "새로운 상태 (UNCONFIRMED, CONFIRMED, RESOLVED)", required = true)
            @RequestParam ObstacleReport.ReportStatus status) {
        
        ObstacleReportResponseDto updatedReport = obstacleReportService.updateObstacleReportStatus(reportId, status);
        return ResponseEntity.ok(updatedReport);
    }

    @GetMapping("/recent")
    @Operation(
            summary = "📅 최근 장애물 신고 조회",
            description = """
                    최근 30일간의 장애물 신고를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 최근 30일간 접수된 모든 장애물 신고를 조회합니다
                    - 신고일시 기준으로 최신순으로 정렬됩니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/recent
                    ```
                    
                    ## 🔍 응답 정보
                    - 최근 30일간의 장애물 신고 목록
                    - 각 신고의 기본 정보 (위치, 종류, 상태, 신고일시)
                    
                    ## ⚠️ 주의사항
                    - 최대 100개의 신고만 반환됩니다
                    - 더 오래된 신고는 조회되지 않습니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최근 장애물 신고 조회 성공",
                    content = @Content(schema = @Schema(implementation = ObstacleReportResponseDto.class)))
    })
    public ResponseEntity<List<ObstacleReportResponseDto>> getRecentObstacles() {
        
        List<ObstacleReportResponseDto> recentObstacles = obstacleReportService.getRecentObstacles();
        return ResponseEntity.ok(recentObstacles);
    }

    @GetMapping("/count/nearby")
    @Operation(
            summary = "🔢 주변 장애물 개수 조회",
            description = """
                    특정 위치 주변의 장애물 신고 개수를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 지정된 위치 주변의 장애물 신고 개수만 반환합니다
                    - 상세 정보 없이 개수만 빠르게 확인할 수 있습니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📍 위치 정보
                    - 위도(latitude): -90 ~ 90 사이의 값
                    - 경도(longitude): -180 ~ 180 사이의 값
                    - 반경(radius): 조회할 반경 (킬로미터 단위)
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/count/nearby?latitude=37.5665&longitude=126.9780&radius=1.0
                    ```
                    
                    ## 🔍 응답 정보
                    - 주변 장애물 신고 개수 (숫자)
                    
                    ## ⚠️ 주의사항
                    - 반경은 0.1km ~ 10km 사이로 설정하는 것을 권장합니다
                    - 성능 최적화를 위해 개수만 반환합니다
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주변 장애물 개수 조회 성공",
                    content = @Content(schema = @Schema(type = "integer", example = "5")))
    })
    public ResponseEntity<Long> getNearbyObstaclesCount(
            @Parameter(description = "위도", required = true, example = "37.5665")
            @RequestParam Double latitude,
            @Parameter(description = "경도", required = true, example = "126.9780")
            @RequestParam Double longitude,
            @Parameter(description = "반경 (km)", required = true, example = "1.0")
            @RequestParam Double radius) {
        
        Long count = obstacleReportService.getNearbyObstaclesCount(latitude, longitude, radius);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/types")
    @Operation(
            summary = "🏷️ 장애물 신고 타입 조회",
            description = """
                    사용 가능한 장애물 신고 타입을 조회합니다.
                    
                    ## 📋 기능 설명
                    - 시스템에서 지원하는 모든 장애물 신고 타입을 조회합니다
                    - 신고 작성 시 참고할 수 있는 타입 목록입니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/types
                    ```
                    
                    ## 🔍 응답 정보
                    - 사용 가능한 장애물 신고 타입 배열
                    - 각 타입의 설명과 용도
                    
                    ## 🏷️ 지원 타입
                    - OBSTACLE: 장애물 (바위, 쓰러진 나무 등)
                    - ROAD_DAMAGE: 도로 손상 (구멍, 균열 등)
                    - CONSTRUCTION: 공사 (도로 공사, 건설 작업 등)
                    - SLIPPERY: 미끄러운 도로 (기름, 얼음 등)
                    - ETC: 기타
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장애물 신고 타입 조회 성공",
                    content = @Content(schema = @Schema(type = "array", example = "[\"OBSTACLE\", \"ROAD_DAMAGE\", \"CONSTRUCTION\", \"SLIPPERY\", \"ETC\"]")))
    })
    public ResponseEntity<ObstacleReport.ReportType[]> getReportTypes() {
        
        ObstacleReport.ReportType[] types = ObstacleReport.ReportType.values();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/statuses")
    @Operation(
            summary = "📊 장애물 신고 상태 조회",
            description = """
                    사용 가능한 장애물 신고 상태를 조회합니다.
                    
                    ## 📋 기능 설명
                    - 시스템에서 지원하는 모든 장애물 신고 상태를 조회합니다
                    - 상태 업데이트 시 참고할 수 있는 상태 목록입니다
                    - 로그인 없이도 조회 가능합니다 (공개 API)
                    
                    ## 📝 사용 예시
                    ```
                    GET /api/v1/obstacles/statuses
                    ```
                    
                    ## 🔍 응답 정보
                    - 사용 가능한 장애물 신고 상태 배열
                    - 각 상태의 의미와 설명
                    
                    ## 📊 지원 상태
                    - UNCONFIRMED: 미확인 (신고 접수됨)
                    - CONFIRMED: 확인됨 (관리자가 확인함)
                    - RESOLVED: 해결됨 (문제가 해결됨)
                    """,
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장애물 신고 상태 조회 성공",
                    content = @Content(schema = @Schema(type = "array", example = "[\"UNCONFIRMED\", \"CONFIRMED\", \"RESOLVED\"]")))
    })
    public ResponseEntity<ObstacleReport.ReportStatus[]> getReportStatuses() {
        
        ObstacleReport.ReportStatus[] statuses = ObstacleReport.ReportStatus.values();
        return ResponseEntity.ok(statuses);
    }
}
