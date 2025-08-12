package com.ll.rideon.domain.riding.controller;

import com.ll.rideon.domain.riding.dto.ObstacleReportRequestDto;
import com.ll.rideon.domain.riding.dto.ObstacleReportResponseDto;
import com.ll.rideon.domain.riding.dto.NearbyObstaclesRequestDto;
import com.ll.rideon.domain.riding.entity.ObstacleReport;
import com.ll.rideon.domain.riding.service.ObstacleReportService;
import com.ll.rideon.global.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "장애물 신고", description = "장애물 신고 관련 API")
public class ObstacleReportController {

    private final ObstacleReportService obstacleReportService;
    private final SecurityUtil securityUtil;

    @PostMapping("/report")
    @Operation(summary = "장애물 신고", description = "라이딩 중 발견한 장애물을 신고합니다")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ObstacleReportResponseDto> reportObstacle(
            @Valid @RequestBody ObstacleReportRequestDto requestDto) {
        
        Long userId = securityUtil.getCurrentUserId();
        ObstacleReportResponseDto response = obstacleReportService.createObstacleReport(userId, requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/nearby")
    @Operation(summary = "주변 장애물 조회", description = "현재 위치 주변의 장애물을 조회합니다")
    public ResponseEntity<List<ObstacleReportResponseDto>> getNearbyObstacles(
            @Valid @ModelAttribute NearbyObstaclesRequestDto requestDto) {
        
        List<ObstacleReportResponseDto> obstacles = obstacleReportService.getNearbyObstacles(requestDto);
        return ResponseEntity.ok(obstacles);
    }

    @GetMapping("/route")
    @Operation(summary = "경로 상 장애물 조회", description = "특정 경로 상의 장애물을 조회합니다")
    public ResponseEntity<List<ObstacleReportResponseDto>> getObstaclesInRoute(
            @Parameter(description = "시작점 위도") @RequestParam Double startLat,
            @Parameter(description = "시작점 경도") @RequestParam Double startLng,
            @Parameter(description = "끝점 위도") @RequestParam Double endLat,
            @Parameter(description = "끝점 경도") @RequestParam Double endLng) {
        
        List<ObstacleReportResponseDto> obstacles = obstacleReportService.getObstaclesInRoute(
                startLat, startLng, endLat, endLng);
        return ResponseEntity.ok(obstacles);
    }

    @GetMapping("/my-reports")
    @Operation(summary = "내 장애물 신고 목록", description = "사용자가 신고한 장애물 목록을 조회합니다")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ObstacleReportResponseDto>> getMyObstacleReports() {
        
        Long userId = securityUtil.getCurrentUserId();
        List<ObstacleReportResponseDto> reports = obstacleReportService.getUserObstacleReports(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "장애물 신고 상세 조회", description = "특정 장애물 신고의 상세 정보를 조회합니다")
    public ResponseEntity<ObstacleReportResponseDto> getObstacleReport(
            @Parameter(description = "장애물 신고 ID") @PathVariable Long reportId) {
        
        ObstacleReportResponseDto report = obstacleReportService.getObstacleReport(reportId);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/{reportId}/status")
    @Operation(summary = "장애물 신고 상태 업데이트", description = "장애물 신고의 상태를 업데이트합니다")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObstacleReportResponseDto> updateObstacleReportStatus(
            @Parameter(description = "장애물 신고 ID") @PathVariable Long reportId,
            @Parameter(description = "새로운 상태") @RequestParam ObstacleReport.ReportStatus status) {
        
        ObstacleReportResponseDto updatedReport = obstacleReportService.updateObstacleReportStatus(reportId, status);
        return ResponseEntity.ok(updatedReport);
    }

    @GetMapping("/recent")
    @Operation(summary = "최근 장애물 신고 조회", description = "최근 30일간의 장애물 신고를 조회합니다")
    public ResponseEntity<List<ObstacleReportResponseDto>> getRecentObstacles() {
        
        List<ObstacleReportResponseDto> recentObstacles = obstacleReportService.getRecentObstacles();
        return ResponseEntity.ok(recentObstacles);
    }

    @GetMapping("/count/nearby")
    @Operation(summary = "주변 장애물 개수 조회", description = "특정 위치 주변의 장애물 신고 개수를 조회합니다")
    public ResponseEntity<Long> getNearbyObstaclesCount(
            @Parameter(description = "위도") @RequestParam Double latitude,
            @Parameter(description = "경도") @RequestParam Double longitude,
            @Parameter(description = "반경 (km)") @RequestParam Double radius) {
        
        Long count = obstacleReportService.getNearbyObstaclesCount(latitude, longitude, radius);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/types")
    @Operation(summary = "장애물 신고 타입 조회", description = "사용 가능한 장애물 신고 타입을 조회합니다")
    public ResponseEntity<ObstacleReport.ReportType[]> getReportTypes() {
        
        ObstacleReport.ReportType[] types = ObstacleReport.ReportType.values();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/statuses")
    @Operation(summary = "장애물 신고 상태 조회", description = "사용 가능한 장애물 신고 상태를 조회합니다")
    public ResponseEntity<ObstacleReport.ReportStatus[]> getReportStatuses() {
        
        ObstacleReport.ReportStatus[] statuses = ObstacleReport.ReportStatus.values();
        return ResponseEntity.ok(statuses);
    }
}
