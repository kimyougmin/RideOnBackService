package com.ll.rideon.domain.riding.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "ride_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RidingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "total_distance_km", precision = 10, scale = 2)
    private BigDecimal totalDistanceKm;

    @Column(name = "avg_speed_kmh", precision = 7, scale = 2)
    private BigDecimal avgSpeedKmh;

    @Column(name = "max_speed_kmh", precision = 7, scale = 2)
    private BigDecimal maxSpeedKmh;

    @Column(name = "calories_burned", precision = 9, scale = 2)
    private BigDecimal caloriesBurned;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RidingStatus status;

    @Column(name = "last_location_lat")
    private Double lastLocationLat;

    @Column(name = "last_location_lng")
    private Double lastLocationLng;

    @Column(name = "last_location_time")
    private LocalDateTime lastLocationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "network_quality")
    private NetworkQuality networkQuality;

    @Column(name = "connection_lost_count")
    private Integer connectionLostCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateLocation(Double lat, Double lng, LocalDateTime time) {
        this.lastLocationLat = lat;
        this.lastLocationLng = lng;
        this.lastLocationTime = time;
    }

    public void updateNetworkQuality(NetworkQuality quality) {
        this.networkQuality = quality;
    }

    public void incrementConnectionLostCount() {
        if (this.connectionLostCount == null) {
            this.connectionLostCount = 0;
        }
        this.connectionLostCount++;
    }

    @Builder
    public RidingSession(Long memberId) {
        this.memberId = memberId;
        this.startedAt = LocalDateTime.now();
        this.status = RidingStatus.ACTIVE;  // ✅ enum 그대로 사용 가능
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
}