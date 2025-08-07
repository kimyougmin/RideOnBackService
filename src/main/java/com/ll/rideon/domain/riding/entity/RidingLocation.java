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
@Table(name = "ride_point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RidingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ride_session_id", nullable = false)
    private Long rideSessionId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "speed_kmh")
    private Float speedKmh;

    @Column(name = "altitude")
    private Float altitude;

    @Column(name = "accuracy")
    private Float accuracy;

    @Column(name = "heading")
    private Float heading;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "network_quality")
    private String networkQuality;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "is_offline_sync")
    private Boolean isOfflineSync;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RidingLocation(Long rideSessionId, Double latitude, Double longitude, 
                         Float speedKmh, Float altitude, Float accuracy, Float heading,
                         LocalDateTime recordedAt, String networkQuality, 
                         Integer batteryLevel, Boolean isOfflineSync) {
        this.rideSessionId = rideSessionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmh = speedKmh;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.heading = heading;
        this.recordedAt = recordedAt;
        this.networkQuality = networkQuality;
        this.batteryLevel = batteryLevel;
        this.isOfflineSync = isOfflineSync != null ? isOfflineSync : false;
    }

    public void markAsSynced() {
        this.isOfflineSync = false;
    }
} 