package com.ll.rideon.domain.riding.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NetworkStatusRequestDto {
    private String connectionType;
    private Integer signalStrength;
    private Boolean isConnected;
    private Long latencyMs;
    private Float uploadSpeedMbps;
    private Float downloadSpeedMbps;
    private Float packetLossPercentage;
    private LocalDateTime recordedAt;
} 