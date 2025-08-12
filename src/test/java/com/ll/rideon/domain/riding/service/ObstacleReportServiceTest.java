package com.ll.rideon.domain.riding.service;

import com.ll.rideon.domain.riding.dto.ObstacleReportRequestDto;
import com.ll.rideon.domain.riding.dto.ObstacleReportResponseDto;
import com.ll.rideon.domain.riding.dto.NearbyObstaclesRequestDto;
import com.ll.rideon.domain.riding.entity.ObstacleReport;
import com.ll.rideon.domain.riding.repository.ObstacleReportRepository;
import com.ll.rideon.global.monitoring.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObstacleReportServiceTest {

    @Mock
    private ObstacleReportRepository obstacleReportRepository;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private ObstacleReportService obstacleReportService;

    private ObstacleReportRequestDto requestDto;
    private ObstacleReport obstacleReport;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        userId = 1L;
        
        // DTO 설정
        requestDto = new ObstacleReportRequestDto();
        setField(requestDto, "latitude", 37.5665);
        setField(requestDto, "longitude", 126.9780);
        setField(requestDto, "reportType", ObstacleReport.ReportType.OBSTACLE);
        setField(requestDto, "description", "도로 중앙에 큰 바위가 있습니다");
        setField(requestDto, "image", "https://example.com/image.jpg");
        
        // 엔티티 설정 (ID 포함)
        obstacleReport = ObstacleReport.builder()
                .userId(userId)
                .latitude(37.5665)
                .longitude(126.9780)
                .reportType(ObstacleReport.ReportType.OBSTACLE)
                .description("도로 중앙에 큰 바위가 있습니다")
                .image("https://example.com/image.jpg")
                .build();
        
        // ID 설정
        setField(obstacleReport, "id", 1L);
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    void createObstacleReport_Success() {
        // given
        when(obstacleReportRepository.save(any(ObstacleReport.class))).thenReturn(obstacleReport);

        // when
        ObstacleReportResponseDto result = obstacleReportService.createObstacleReport(userId, requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getReportType()).isEqualTo(ObstacleReport.ReportType.OBSTACLE);
        assertThat(result.getStatus()).isEqualTo(ObstacleReport.ReportStatus.UNCONFIRMED);

        verify(obstacleReportRepository).save(any(ObstacleReport.class));
        verify(metricsService).incrementObstacleReportCount(anyString());
    }

    @Test
    void getNearbyObstacles_Success() throws Exception {
        // given
        NearbyObstaclesRequestDto requestDto = new NearbyObstaclesRequestDto();
        setField(requestDto, "latitude", 37.5665);
        setField(requestDto, "longitude", 126.9780);
        setField(requestDto, "radius", 5.0);
        
        List<ObstacleReport> nearbyObstacles = Arrays.asList(obstacleReport);
        
        when(obstacleReportRepository.findNearbyObstacles(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(nearbyObstacles);

        // when
        List<ObstacleReportResponseDto> result = obstacleReportService.getNearbyObstacles(requestDto);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        
        verify(obstacleReportRepository).findNearbyObstacles(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void getObstaclesInRoute_Success() {
        // given
        Double startLat = 37.5665;
        Double startLng = 126.9780;
        Double endLat = 37.5666;
        Double endLng = 126.9781;
        
        List<ObstacleReport> routeObstacles = Arrays.asList(obstacleReport);
        
        when(obstacleReportRepository.findObstaclesInRoute(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(routeObstacles);

        // when
        List<ObstacleReportResponseDto> result = obstacleReportService.getObstaclesInRoute(
                startLat, startLng, endLat, endLng);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        
        verify(obstacleReportRepository).findObstaclesInRoute(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void getUserObstacleReports_Success() {
        // given
        List<ObstacleReport> userReports = Arrays.asList(obstacleReport);
        
        when(obstacleReportRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(userReports);

        // when
        List<ObstacleReportResponseDto> result = obstacleReportService.getUserObstacleReports(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        
        verify(obstacleReportRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void getObstacleReport_Success() {
        // given
        Long reportId = 1L;
        
        when(obstacleReportRepository.findById(reportId))
                .thenReturn(Optional.of(obstacleReport));

        // when
        ObstacleReportResponseDto result = obstacleReportService.getObstacleReport(reportId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reportId);
        assertThat(result.getUserId()).isEqualTo(userId);
        
        verify(obstacleReportRepository).findById(reportId);
    }

    @Test
    void getObstacleReport_NotFound() {
        // given
        Long reportId = 999L;
        
        when(obstacleReportRepository.findById(reportId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> obstacleReportService.getObstacleReport(reportId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 장애물 신고입니다");
        
        verify(obstacleReportRepository).findById(reportId);
    }

    @Test
    void updateObstacleReportStatus_Success() {
        // given
        Long reportId = 1L;
        ObstacleReport.ReportStatus newStatus = ObstacleReport.ReportStatus.CONFIRMED;
        
        when(obstacleReportRepository.findById(reportId))
                .thenReturn(Optional.of(obstacleReport));
        when(obstacleReportRepository.save(any(ObstacleReport.class)))
                .thenReturn(obstacleReport);

        // when
        ObstacleReportResponseDto result = obstacleReportService.updateObstacleReportStatus(reportId, newStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reportId);
        
        verify(obstacleReportRepository).findById(reportId);
        verify(obstacleReportRepository).save(any(ObstacleReport.class));
    }

    @Test
    void getRecentObstacles_Success() {
        // given
        List<ObstacleReport> recentObstacles = Arrays.asList(obstacleReport);
        
        when(obstacleReportRepository.findRecentObstacles(any(LocalDateTime.class)))
                .thenReturn(recentObstacles);

        // when
        List<ObstacleReportResponseDto> result = obstacleReportService.getRecentObstacles();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        
        verify(obstacleReportRepository).findRecentObstacles(any(LocalDateTime.class));
    }

    @Test
    void getNearbyObstaclesCount_Success() {
        // given
        Double latitude = 37.5665;
        Double longitude = 126.9780;
        Double radius = 5.0;
        Long expectedCount = 3L;
        
        when(obstacleReportRepository.countNearbyObstacles(latitude, longitude, radius))
                .thenReturn(expectedCount);

        // when
        Long result = obstacleReportService.getNearbyObstaclesCount(latitude, longitude, radius);

        // then
        assertThat(result).isEqualTo(expectedCount);
        
        verify(obstacleReportRepository).countNearbyObstacles(latitude, longitude, radius);
    }
}
