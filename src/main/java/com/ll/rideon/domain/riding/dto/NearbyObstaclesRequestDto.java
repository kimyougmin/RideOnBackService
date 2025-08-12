package com.ll.rideon.domain.riding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;

@Getter
@NoArgsConstructor
@Schema(description = "주변 장애물 조회 요청 DTO")
public class NearbyObstaclesRequestDto {
    
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다")
    @Schema(description = "현재 위치 위도", example = "37.5665", required = true)
    private Double latitude;
    
    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다")
    @Schema(description = "현재 위치 경도", example = "126.9780", required = true)
    private Double longitude;
    
    @NotNull(message = "반경은 필수입니다")
    @Min(value = 1, message = "반경은 1km 이상이어야 합니다")
    @Schema(description = "조회 반경 (km)", example = "5.0", required = true)
    private Double radius;
}
