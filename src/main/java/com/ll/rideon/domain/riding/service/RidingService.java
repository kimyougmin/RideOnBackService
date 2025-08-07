package com.ll.rideon.domain.riding.service;

import com.ll.rideon.domain.riding.dto.LocationUpdateRequestDto;
import com.ll.rideon.domain.riding.dto.NetworkRecommendation;
import com.ll.rideon.domain.riding.dto.NetworkStatusRequestDto;
import com.ll.rideon.domain.riding.dto.RidingSessionCreateRequestDto;
import com.ll.rideon.domain.riding.dto.RidingSessionResponseDto;
import com.ll.rideon.domain.riding.entity.NetworkStatus;
import com.ll.rideon.domain.riding.entity.RidingLocation;
import com.ll.rideon.domain.riding.entity.RidingSession;
import com.ll.rideon.domain.riding.entity.RidingStatus;
import com.ll.rideon.domain.riding.repository.NetworkStatusRepository;
import com.ll.rideon.domain.riding.repository.RidingLocationRepository;
import com.ll.rideon.domain.riding.repository.RidingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RidingService {

    private final RidingSessionRepository ridingSessionRepository;
    private final RidingLocationRepository ridingLocationRepository;
    private final NetworkStatusRepository networkStatusRepository;
    private final NetworkMonitoringService networkMonitoringService;

    @Transactional
    public RidingSessionResponseDto createRidingSession(Long userId, RidingSessionCreateRequestDto requestDto) {
        // 기존 활성 세션이 있는지 확인
        ridingSessionRepository.findActiveSessionByUserId(userId)
                .ifPresent(session -> {
                    throw new IllegalStateException("이미 진행 중인 라이딩 세션이 있습니다.");
                });

        RidingSession session = RidingSession.builder()
                .userId(userId)
                .build();

        RidingSession savedSession = ridingSessionRepository.save(session);
        log.info("라이딩 세션 생성: userId={}, sessionId={}", userId, savedSession.getId());
        
        return RidingSessionResponseDto.from(savedSession);
    }

    @Transactional
    public void updateLocation(Long sessionId, LocationUpdateRequestDto requestDto) {
        RidingSession session = ridingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("라이딩 세션을 찾을 수 없습니다."));

        // 위치 정보 저장
        RidingLocation location = RidingLocation.builder()
                .rideSessionId(sessionId)
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .speedKmh(requestDto.getSpeedKmh())
                .altitude(requestDto.getAltitude())
                .accuracy(requestDto.getAccuracy())
                .heading(requestDto.getHeading())
                .recordedAt(requestDto.getRecordedAt() != null ? requestDto.getRecordedAt() : LocalDateTime.now())
                .networkQuality(requestDto.getNetworkQuality())
                .batteryLevel(requestDto.getBatteryLevel())
                .isOfflineSync(requestDto.getIsOfflineSync())
                .build();

        ridingLocationRepository.save(location);

        // 세션의 마지막 위치 업데이트
        session.updateLocation(requestDto.getLatitude(), requestDto.getLongitude(), location.getRecordedAt());

        // 네트워크 품질 업데이트
        if (requestDto.getNetworkQuality() != null) {
            session.updateNetworkQuality(requestDto.getNetworkQuality());
        }

        log.debug("위치 업데이트: sessionId={}, lat={}, lng={}, speed={}", 
                sessionId, requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getSpeedKmh());
    }

    @Transactional
    public void updateNetworkStatus(Long sessionId, NetworkStatusRequestDto requestDto) {
        RidingSession session = ridingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("라이딩 세션을 찾을 수 없습니다."));

        NetworkStatus networkStatus = NetworkStatus.builder()
                .rideSessionId(sessionId)
                .connectionType(requestDto.getConnectionType())
                .signalStrength(requestDto.getSignalStrength())
                .isConnected(requestDto.getIsConnected())
                .latencyMs(requestDto.getLatencyMs())
                .uploadSpeedMbps(requestDto.getUploadSpeedMbps())
                .downloadSpeedMbps(requestDto.getDownloadSpeedMbps())
                .packetLossPercentage(requestDto.getPacketLossPercentage())
                .recordedAt(requestDto.getRecordedAt() != null ? requestDto.getRecordedAt() : LocalDateTime.now())
                .build();

        // 네트워크 모니터링 서비스에 상태 기록
        networkMonitoringService.recordNetworkStatus(sessionId, networkStatus);
    }

    @Transactional
    public void endRidingSession(Long sessionId) {
        RidingSession session = ridingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("라이딩 세션을 찾을 수 없습니다."));

        session.endSession();
        
        // 오프라인 동기화 데이터 처리
        List<RidingLocation> offlineLocations = ridingLocationRepository
                .findOfflineSyncLocationsBySessionId(sessionId);
        
        if (!offlineLocations.isEmpty()) {
            log.info("오프라인 동기화 데이터 처리: sessionId={}, count={}", sessionId, offlineLocations.size());
            ridingLocationRepository.markAllAsSynced(sessionId);
        }

        log.info("라이딩 세션 종료: sessionId={}", sessionId);
    }

    @Transactional
    public void pauseRidingSession(Long sessionId) {
        RidingSession session = ridingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("라이딩 세션을 찾을 수 없습니다."));

        session.pauseSession();
        log.info("라이딩 세션 일시정지: sessionId={}", sessionId);
    }

    @Transactional
    public void resumeRidingSession(Long sessionId) {
        RidingSession session = ridingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("라이딩 세션을 찾을 수 없습니다."));

        session.resumeSession();
        log.info("라이딩 세션 재개: sessionId={}", sessionId);
    }

    public RidingSessionResponseDto getRidingSession(Long sessionId) {
        RidingSession session = ridingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("라이딩 세션을 찾을 수 없습니다."));

        return RidingSessionResponseDto.from(session);
    }

    public Page<RidingSessionResponseDto> getUserRidingSessions(Long userId, Pageable pageable) {
        Page<RidingSession> sessions = ridingSessionRepository.findByUserIdOrderByStartedAtDesc(userId, pageable);
        return sessions.map(RidingSessionResponseDto::from);
    }

    public List<RidingLocation> getRidingLocations(Long sessionId) {
        return ridingLocationRepository.findByRideSessionIdOrderByRecordedAtAsc(sessionId);
    }

    public NetworkRecommendation getNetworkRecommendation(Long sessionId) {
        return networkMonitoringService.getNetworkRecommendation(sessionId);
    }

    public RidingSessionResponseDto getActiveSession(Long userId) {
        RidingSession session = ridingSessionRepository.findActiveSessionByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 라이딩 세션이 없습니다."));

        return RidingSessionResponseDto.from(session);
    }

    @Transactional
    public void syncOfflineData(Long sessionId) {
        List<RidingLocation> offlineLocations = ridingLocationRepository
                .findOfflineSyncLocationsBySessionId(sessionId);

        if (!offlineLocations.isEmpty()) {
            log.info("오프라인 데이터 동기화: sessionId={}, count={}", sessionId, offlineLocations.size());
            ridingLocationRepository.markAllAsSynced(sessionId);
        }
    }
} 