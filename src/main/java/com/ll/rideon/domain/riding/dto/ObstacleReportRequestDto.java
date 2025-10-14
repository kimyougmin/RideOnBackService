package com.ll.rideon.domain.riding.dto;

import com.ll.rideon.domain.riding.entity.ObstacleReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

@Getter
@NoArgsConstructor
@Schema(description = "장애물 신고 요청 DTO")
public class ObstacleReportRequestDto {
    
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다")
    @Schema(description = "위도", example = "37.5665", required = true)
    private Double latitude;
    
    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다")
    @Schema(description = "경도", example = "126.9780", required = true)
    private Double longitude;
    
    @NotNull(message = "신고 타입은 필수입니다")
    @Schema(description = "신고 타입", example = "OBSTACLE", required = true, 
            allowableValues = {"OBSTACLE", "ROAD_DAMAGE", "CONSTRUCTION", "SLIPPERY", "ETC"})
    private ObstacleReport.ReportType reportType;
    
    @Schema(description = "장애물 설명", example = "도로 중앙에 큰 바위가 있습니다")
    private String description;
    
    @Schema(description = "장애물 이미지 URL", example = "https://example.com/image.jpg")
    private String image;
}
