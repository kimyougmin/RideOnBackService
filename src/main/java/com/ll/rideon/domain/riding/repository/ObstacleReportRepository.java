package com.ll.rideon.domain.riding.repository;

import com.ll.rideon.domain.riding.entity.ObstacleReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ObstacleReportRepository extends JpaRepository<ObstacleReport, Long> {

    // 사용자별 장애물 신고 목록 조회
    @Query("SELECT o FROM ObstacleReport o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<ObstacleReport> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 특정 위치 주변의 장애물 신고 조회 (반경 기반)
    @Query("SELECT o FROM ObstacleReport o WHERE " +
           "SQRT(POWER(o.latitude - :latitude, 2) + POWER(o.longitude - :longitude, 2)) <= :radius " +
           "ORDER BY o.createdAt DESC")
    List<ObstacleReport> findNearbyObstacles(@Param("latitude") Double latitude, 
                                           @Param("longitude") Double longitude, 
                                           @Param("radius") Double radius);

    // 특정 경로 상의 장애물 신고 조회 (경로의 시작점과 끝점 사이)
    @Query("SELECT o FROM ObstacleReport o WHERE " +
           "o.latitude BETWEEN :minLat AND :maxLat AND " +
           "o.longitude BETWEEN :minLng AND :maxLng " +
           "ORDER BY o.createdAt DESC")
    List<ObstacleReport> findObstaclesInRoute(@Param("minLat") Double minLat, 
                                            @Param("maxLat") Double maxLat,
                                            @Param("minLng") Double minLng, 
                                            @Param("maxLng") Double maxLng);

    // 상태별 장애물 신고 조회
    @Query("SELECT o FROM ObstacleReport o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<ObstacleReport> findByStatusOrderByCreatedAtDesc(@Param("status") ObstacleReport.ReportStatus status);

    // 타입별 장애물 신고 조회
    @Query("SELECT o FROM ObstacleReport o WHERE o.reportType = :reportType ORDER BY o.createdAt DESC")
    List<ObstacleReport> findByReportTypeOrderByCreatedAtDesc(@Param("reportType") ObstacleReport.ReportType reportType);

    // 최근 장애물 신고 조회 (최근 30일) - H2 호환성
    @Query("SELECT o FROM ObstacleReport o WHERE o.createdAt >= :thirtyDaysAgo ORDER BY o.createdAt DESC")
    List<ObstacleReport> findRecentObstacles(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    // 특정 지역의 장애물 신고 개수 조회
    @Query("SELECT COUNT(o) FROM ObstacleReport o WHERE " +
           "SQRT(POWER(o.latitude - :latitude, 2) + POWER(o.longitude - :longitude, 2)) <= :radius")
    Long countNearbyObstacles(@Param("latitude") Double latitude, 
                             @Param("longitude") Double longitude, 
                             @Param("radius") Double radius);
}
