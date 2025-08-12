package com.ll.rideon.domain.riding.dto;

import com.ll.rideon.domain.riding.entity.ObstacleReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "장애물 신고 응답 DTO")
public class ObstacleReportResponseDto {
    
    @Schema(description = "장애물 신고 ID", example = "1")
    private Long id;
    
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    
    @Schema(description = "위도", example = "37.5665")
    private Double latitude;
    
    @Schema(description = "경도", example = "126.9780")
    private Double longitude;
    
    @Schema(description = "신고 타입", example = "OBSTACLE")
    private ObstacleReport.ReportType reportType;
    
    @Schema(description = "신고 타입 설명", example = "장애물")
    private String reportTypeDescription;
    
    @Schema(description = "장애물 설명", example = "도로 중앙에 큰 바위가 있습니다")
    private String description;
    
    @Schema(description = "신고 상태", example = "UNCONFIRMED")
    private ObstacleReport.ReportStatus status;
    
    @Schema(description = "신고 상태 설명", example = "미확인")
    private String statusDescription;
    
    @Schema(description = "장애물 이미지 URL", example = "https://example.com/image.jpg")
    private String image;
    
    @Schema(description = "생성 시간", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;
    
    public static ObstacleReportResponseDto from(ObstacleReport obstacleReport) {
        return ObstacleReportResponseDto.builder()
                .id(obstacleReport.getId())
                .userId(obstacleReport.getUserId())
                .latitude(obstacleReport.getLatitude())
                .longitude(obstacleReport.getLongitude())
                .reportType(obstacleReport.getReportType())
                .reportTypeDescription(obstacleReport.getReportType().getDescription())
                .description(obstacleReport.getDescription())
                .status(obstacleReport.getStatus())
                .statusDescription(obstacleReport.getStatus().getDescription())
                .image(obstacleReport.getImage())
                .createdAt(obstacleReport.getCreatedAt())
                .build();
    }
}
