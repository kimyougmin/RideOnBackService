package com.ll.rideon.domain.riding.service;

import com.ll.rideon.domain.riding.dto.ObstacleReportRequestDto;
import com.ll.rideon.domain.riding.dto.ObstacleReportResponseDto;
import com.ll.rideon.domain.riding.dto.NearbyObstaclesRequestDto;
import com.ll.rideon.domain.riding.entity.ObstacleReport;
import com.ll.rideon.domain.riding.repository.ObstacleReportRepository;
import com.ll.rideon.global.monitoring.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ObstacleReportService {

    private final ObstacleReportRepository obstacleReportRepository;
    private final MetricsService metricsService;

    /**
     * 장애물 신고 생성
     */
    @Transactional
    public ObstacleReportResponseDto createObstacleReport(Long userId, ObstacleReportRequestDto requestDto) {
        log.info("장애물 신고 생성 요청 - 사용자 ID: {}, 위치: ({}, {})", 
                userId, requestDto.getLatitude(), requestDto.getLongitude());

        ObstacleReport obstacleReport = ObstacleReport.builder()
                .userId(userId)
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .reportType(requestDto.getReportType())
                .description(requestDto.getDescription())
                .image(requestDto.getImage())
                .build();

        ObstacleReport savedReport = obstacleReportRepository.save(obstacleReport);
        
        // 메트릭 업데이트
        metricsService.incrementObstacleReportCount(requestDto.getReportType().name());
        
        log.info("장애물 신고 생성 완료 - ID: {}", savedReport.getId());
        return ObstacleReportResponseDto.from(savedReport);
    }

    /**
     * 주변 장애물 조회
     */
    public List<ObstacleReportResponseDto> getNearbyObstacles(NearbyObstaclesRequestDto requestDto) {
        log.info("주변 장애물 조회 요청 - 위치: ({}, {}), 반경: {}km", 
                requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getRadius());

        List<ObstacleReport> nearbyObstacles = obstacleReportRepository.findNearbyObstacles(
                requestDto.getLatitude(), 
                requestDto.getLongitude(), 
                requestDto.getRadius()
        );

        log.info("주변 장애물 조회 완료 - {}개 발견", nearbyObstacles.size());
        return nearbyObstacles.stream()
                .map(ObstacleReportResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 경로 상의 장애물 조회
     */
    public List<ObstacleReportResponseDto> getObstaclesInRoute(Double startLat, Double startLng, 
                                                             Double endLat, Double endLng) {
        log.info("경로 상 장애물 조회 요청 - 시작: ({}, {}), 끝: ({}, {})", 
                startLat, startLng, endLat, endLng);

        // 경로의 경계 계산
        Double minLat = Math.min(startLat, endLat);
        Double maxLat = Math.max(startLat, endLat);
        Double minLng = Math.min(startLng, endLng);
        Double maxLng = Math.max(startLng, endLng);

        List<ObstacleReport> routeObstacles = obstacleReportRepository.findObstaclesInRoute(
                minLat, maxLat, minLng, maxLng
        );

        log.info("경로 상 장애물 조회 완료 - {}개 발견", routeObstacles.size());
        return routeObstacles.stream()
                .map(ObstacleReportResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자별 장애물 신고 목록 조회
     */
    public List<ObstacleReportResponseDto> getUserObstacleReports(Long userId) {
        log.info("사용자 장애물 신고 목록 조회 - 사용자 ID: {}", userId);

        List<ObstacleReport> userReports = obstacleReportRepository.findByUserIdOrderByCreatedAtDesc(userId);

        log.info("사용자 장애물 신고 목록 조회 완료 - {}개", userReports.size());
        return userReports.stream()
                .map(ObstacleReportResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 장애물 신고 상세 조회
     */
    public ObstacleReportResponseDto getObstacleReport(Long reportId) {
        log.info("장애물 신고 상세 조회 - 신고 ID: {}", reportId);

        ObstacleReport obstacleReport = obstacleReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장애물 신고입니다: " + reportId));

        return ObstacleReportResponseDto.from(obstacleReport);
    }

    /**
     * 장애물 신고 상태 업데이트
     */
    @Transactional
    public ObstacleReportResponseDto updateObstacleReportStatus(Long reportId, ObstacleReport.ReportStatus status) {
        log.info("장애물 신고 상태 업데이트 - 신고 ID: {}, 상태: {}", reportId, status);

        ObstacleReport obstacleReport = obstacleReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장애물 신고입니다: " + reportId));

        obstacleReport.updateStatus(status);
        ObstacleReport savedReport = obstacleReportRepository.save(obstacleReport);

        log.info("장애물 신고 상태 업데이트 완료 - 신고 ID: {}", reportId);
        return ObstacleReportResponseDto.from(savedReport);
    }

    /**
     * 최근 장애물 신고 조회
     */
    public List<ObstacleReportResponseDto> getRecentObstacles() {
        log.info("최근 장애물 신고 조회 요청");

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<ObstacleReport> recentObstacles = obstacleReportRepository.findRecentObstacles(thirtyDaysAgo);

        log.info("최근 장애물 신고 조회 완료 - {}개", recentObstacles.size());
        return recentObstacles.stream()
                .map(ObstacleReportResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 지역의 장애물 신고 개수 조회
     */
    public Long getNearbyObstaclesCount(Double latitude, Double longitude, Double radius) {
        log.info("특정 지역 장애물 신고 개수 조회 - 위치: ({}, {}), 반경: {}km", latitude, longitude, radius);

        Long count = obstacleReportRepository.countNearbyObstacles(latitude, longitude, radius);

        log.info("특정 지역 장애물 신고 개수 조회 완료 - {}개", count);
        return count;
    }
}
