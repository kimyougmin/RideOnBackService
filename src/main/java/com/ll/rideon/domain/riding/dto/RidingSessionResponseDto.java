package com.ll.rideon.domain.riding.dto;

import com.ll.rideon.domain.riding.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class RidingSessionResponseDto {
    private Long id;
    private Long memberId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal totalDistanceKm;
    private BigDecimal avgSpeedKmh;
    private BigDecimal maxSpeedKmh;
    private BigDecimal caloriesBurned;
    private RidingStatus status;
    private Double lastLocationLat;
    private Double lastLocationLng;
    private LocalDateTime lastLocationTime;
    private NetworkQuality networkQuality;
    private Integer connectionLostCount;
    private LocalDateTime createdAt;

    public static RidingSessionResponseDto from(RidingSession session) {
        return RidingSessionResponseDto.builder()
                .id(session.getId())
                .memberId(session.getMemberId())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .totalDistanceKm(session.getTotalDistanceKm())
                .avgSpeedKmh(session.getAvgSpeedKmh())
                .maxSpeedKmh(session.getMaxSpeedKmh())
                .caloriesBurned(session.getCaloriesBurned())
                .status(session.getStatus())
                .lastLocationLat(session.getLastLocationLat())
                .lastLocationLng(session.getLastLocationLng())
                .lastLocationTime(session.getLastLocationTime())
                .networkQuality(session.getNetworkQuality())
                .connectionLostCount(session.getConnectionLostCount())
                .createdAt(session.getCreatedAt())
                .build();
    }
} 