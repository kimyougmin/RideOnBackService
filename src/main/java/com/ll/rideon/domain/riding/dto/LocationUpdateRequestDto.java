package com.ll.rideon.domain.riding.dto;

import com.ll.rideon.domain.riding.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "위치 정보 업데이트 요청 DTO")
public class LocationUpdateRequestDto {
    
    @Schema(description = "위도", example = "37.5665", required = true)
    private Double latitude;
    
    @Schema(description = "경도", example = "126.9780", required = true)
    private Double longitude;
    
    @Schema(description = "속도 (km/h)", example = "25.5")
    private Float speedKmh;
    
    @Schema(description = "고도 (미터)", example = "45.2")
    private Float altitude;
    
    @Schema(description = "정확도 (미터)", example = "5.0")
    private Float accuracy;
    
    @Schema(description = "방향 (도)", example = "180.0")
    private Float heading;
    
    @Schema(description = "기록 시간", example = "2024-01-01T12:00:00")
    private LocalDateTime recordedAt;
    
    @Schema(description = "네트워크 품질", example = "GOOD", allowableValues = {"POOR", "FAIR", "GOOD", "EXCELLENT"})
    private NetworkQuality networkQuality;
    
    @Schema(description = "배터리 레벨 (%)", example = "85")
    private Integer batteryLevel;
    
    @Schema(description = "오프라인 동기화 여부", example = "false")
    private Boolean isOfflineSync;
} 