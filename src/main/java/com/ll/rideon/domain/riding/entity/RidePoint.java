package com.ll.rideon.domain.riding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "ride_point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RidePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ride_session_id", nullable = false)
    private Long rideSessionId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "speed_kmh", precision = 7, scale = 2)
    private BigDecimal speedKmh;

    @Column(name = "altitude", precision = 9, scale = 2)
    private BigDecimal altitude;

    @Column(name = "accuracy", precision = 9, scale = 3)
    private BigDecimal accuracy;

    @Column(name = "heading", precision = 7, scale = 3)
    private BigDecimal heading;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "network_quality")
    private NetworkQuality networkQuality;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "is_offline_sync")
    private Boolean isOfflineSync;

    @Builder
    public RidePoint(Long rideSessionId, Double latitude, Double longitude,
                     BigDecimal altitude, BigDecimal speedKmh, BigDecimal heading, BigDecimal accuracy,
                    NetworkQuality networkQuality, LocalDateTime recordedAt, Integer batteryLevel, Boolean isOfflineSync) {
        this.rideSessionId = rideSessionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speedKmh = speedKmh;
        this.heading = heading;
        this.accuracy = accuracy;
        this.networkQuality = networkQuality;
        this.recordedAt = recordedAt;
        this.batteryLevel = batteryLevel;
        this.isOfflineSync = isOfflineSync;
    }
}
