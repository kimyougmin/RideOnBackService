package com.ll.rideon.domain.riding.dto;

import com.ll.rideon.domain.riding.entity.RidingSession;
import com.ll.rideon.domain.riding.entity.RidingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RidingSessionResponseDto {
    private Long id;
    private Long userId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Float totalDistanceKm;
    private Float avgSpeedKmh;
    private Float maxSpeedKmh;
    private Float caloriesBurned;
    private RidingStatus status;
    private Double lastLocationLat;
    private Double lastLocationLng;
    private LocalDateTime lastLocationTime;
    private String networkQuality;
    private Integer connectionLostCount;
    private LocalDateTime createdAt;

    public static RidingSessionResponseDto from(RidingSession session) {
        return RidingSessionResponseDto.builder()
                .id(session.getId())
                .userId(session.getUserId())
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