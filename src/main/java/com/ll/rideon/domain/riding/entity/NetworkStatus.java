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
@Table(name = "network_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class NetworkStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ride_session_id", nullable = false)
    private Long rideSessionId;

    @Column(name = "connection_type")
    private String connectionType; // WIFI, 4G, 5G, 3G, NONE

    @Column(name = "signal_strength")
    private Integer signalStrength; // 0-100

    @Column(name = "is_connected")
    private Boolean isConnected;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "upload_speed_mbps")
    private Float uploadSpeedMbps;

    @Column(name = "download_speed_mbps")
    private Float downloadSpeedMbps;

    @Column(name = "packet_loss_percentage")
    private Float packetLossPercentage;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public NetworkStatus(Long rideSessionId, String connectionType, Integer signalStrength,
                       Boolean isConnected, Long latencyMs, Float uploadSpeedMbps,
                       Float downloadSpeedMbps, Float packetLossPercentage, LocalDateTime recordedAt) {
        this.rideSessionId = rideSessionId;
        this.connectionType = connectionType;
        this.signalStrength = signalStrength;
        this.isConnected = isConnected;
        this.latencyMs = latencyMs;
        this.uploadSpeedMbps = uploadSpeedMbps;
        this.downloadSpeedMbps = downloadSpeedMbps;
        this.packetLossPercentage = packetLossPercentage;
        this.recordedAt = recordedAt;
    }
} 