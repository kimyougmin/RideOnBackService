package com.ll.rideon.domain.riding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RidingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "total_distance_km")
    private Float totalDistanceKm;

    @Column(name = "avg_speed_kmh")
    private Float avgSpeedKmh;

    @Column(name = "max_speed_kmh")
    private Float maxSpeedKmh;

    @Column(name = "calories_burned")
    private Float caloriesBurned;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RidingStatus status;

    @Column(name = "last_location_lat")
    private Double lastLocationLat;

    @Column(name = "last_location_lng")
    private Double lastLocationLng;

    @Column(name = "last_location_time")
    private LocalDateTime lastLocationTime;

    @Column(name = "network_quality")
    private String networkQuality;

    @Column(name = "connection_lost_count")
    private Integer connectionLostCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RidingSession(Long userId) {
        this.userId = userId;
        this.startedAt = LocalDateTime.now();
        this.status = RidingStatus.ACTIVE;
        this.connectionLostCount = 0;
    }

    public void endSession() {
        this.endedAt = LocalDateTime.now();
        this.status = RidingStatus.COMPLETED;
    }

    public void pauseSession() {
        this.status = RidingStatus.PAUSED;
    }

    public void resumeSession() {
        this.status = RidingStatus.ACTIVE;
    }

    public void updateLocation(Double lat, Double lng, LocalDateTime time) {
        this.lastLocationLat = lat;
        this.lastLocationLng = lng;
        this.lastLocationTime = time;
    }

    public void updateNetworkQuality(String quality) {
        this.networkQuality = quality;
    }

    public void incrementConnectionLostCount() {
        this.connectionLostCount++;
    }

    public void updateStats(Float totalDistance, Float avgSpeed, Float maxSpeed, Float calories) {
        this.totalDistanceKm = totalDistance;
        this.avgSpeedKmh = avgSpeed;
        this.maxSpeedKmh = maxSpeed;
        this.caloriesBurned = calories;
    }
} 