package com.ll.rideon.domain.riding.dto;

import com.ll.rideon.domain.riding.service.NetworkMonitoringService.NetworkPriority;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NetworkRecommendation {
    private String action;
    private String message;
    private NetworkPriority priority;
} 