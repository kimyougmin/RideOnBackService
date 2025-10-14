package com.ll.rideon.domain.riding.service;

import com.ll.rideon.domain.riding.dto.NetworkRecommendation;
import com.ll.rideon.domain.riding.entity.*;
import com.ll.rideon.domain.riding.repository.NetworkStatusRepository;
import com.ll.rideon.domain.riding.repository.RidingSessionRepository;
import com.ll.rideon.global.monitoring.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NetworkMonitoringService {

    private final NetworkStatusRepository networkStatusRepository;
    private final RidingSessionRepository ridingSessionRepository;
    private final MetricsService metricsService;

    @Transactional
    public void recordNetworkStatus(Long sessionId, NetworkStatus networkStatus) {
        // 네트워크 상태 저장
        networkStatusRepository.save(networkStatus);

        // 연결 상태가 끊어진 경우 세션에 기록
        if (!networkStatus.getIsConnected()) {
            RidingSession session = ridingSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("라이딩 세션을 찾을 수 없습니다."));
            
            session.incrementConnectionLostCount();
            session.updateNetworkQuality(NetworkQuality.POOR);
            
            // 메트릭 기록
            metricsService.incrementNetworkDisconnection();
            
            log.warn("라이딩 세션 {} 연결 끊김 감지", sessionId);
        }

        // 네트워크 품질이 낮은 경우 경고
        if (networkStatus.getSignalStrength() != null && networkStatus.getSignalStrength() < 30) {
            log.warn("라이딩 세션 {} 신호 강도 낮음: {}%", sessionId, networkStatus.getSignalStrength());
        }

        // 패킷 손실이 높은 경우 경고
        if (networkStatus.getPacketLossPercentage() != null && networkStatus.getPacketLossPercentage() > 5.0f) {
            log.warn("라이딩 세션 {} 패킷 손실 높음: {}%", sessionId, networkStatus.getPacketLossPercentage());
        }

        // 네트워크 품질 변경 감지 및 메트릭 기록
        NetworkQuality currentQuality = assessNetworkQuality(sessionId);
        if (currentQuality != NetworkQuality.UNKNOWN) {
            metricsService.incrementNetworkQualityChange();
        }
    }

    public NetworkQuality assessNetworkQuality(Long sessionId) {
        List<NetworkStatus> recentStatuses = networkStatusRepository
                .findByRideSessionIdOrderByRecordedAtDesc(sessionId);

        if (recentStatuses.isEmpty()) {
            return NetworkQuality.UNKNOWN;
        }

        NetworkStatus latest = recentStatuses.get(0);
        
        // 연결이 끊어진 경우
        if (!latest.getIsConnected()) {
            return NetworkQuality.POOR;
        }

        // 신호 강도 기반 평가
        if (latest.getSignalStrength() != null) {
            if (latest.getSignalStrength() < 20) {
                return NetworkQuality.POOR;
            } else if (latest.getSignalStrength() < 50) {
                return NetworkQuality.FAIR;
            } else if (latest.getSignalStrength() < 80) {
                return NetworkQuality.GOOD;
            } else {
                return NetworkQuality.EXCELLENT;
            }
        }

        // 패킷 손실 기반 평가
        if (latest.getPacketLossPercentage() != null) {
            if (latest.getPacketLossPercentage() > 10.0f) {
                return NetworkQuality.POOR;
            } else if (latest.getPacketLossPercentage() > 5.0f) {
                return NetworkQuality.FAIR;
            }
        }

        return NetworkQuality.GOOD;
    }

    public NetworkRecommendation getNetworkRecommendation(Long sessionId) {
        NetworkQuality quality = assessNetworkQuality(sessionId);
        
        switch (quality) {
            case UNKNOWN:
                return NetworkRecommendation.builder()
                        .action("OFFLINE_MODE")
                        .message("네트워크 상태를 알 수 없습니다. 오프라인 모드로 전환합니다.")
                        .priority(NetworkPriority.HIGH)
                        .build();
            case POOR:
                return NetworkRecommendation.builder()
                        .action("REDUCE_FREQUENCY")
                        .message("네트워크 상태가 불안정합니다. 데이터 전송 빈도를 줄입니다.")
                        .priority(NetworkPriority.MEDIUM)
                        .build();
            case FAIR:
                return NetworkRecommendation.builder()
                        .action("MONITOR")
                        .message("네트워크 상태를 모니터링합니다.")
                        .priority(NetworkPriority.LOW)
                        .build();
            case GOOD:
            case EXCELLENT:
                return NetworkRecommendation.builder()
                        .action("NORMAL")
                        .message("네트워크 상태가 양호합니다.")
                        .priority(NetworkPriority.LOW)
                        .build();
            default:
                return NetworkRecommendation.builder()
                        .action("UNKNOWN")
                        .message("네트워크 상태를 확인할 수 없습니다.")
                        .priority(NetworkPriority.MEDIUM)
                        .build();
        }
    }
} 