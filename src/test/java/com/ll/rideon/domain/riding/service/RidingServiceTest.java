package com.ll.rideon.domain.riding.service;

import com.ll.rideon.domain.riding.dto.RidingSessionCreateRequestDto;
import com.ll.rideon.domain.riding.entity.RidingSession;
import com.ll.rideon.domain.riding.entity.RidingStatus;
import com.ll.rideon.domain.riding.repository.RidingLocationRepository;
import com.ll.rideon.domain.riding.repository.RidingSessionRepository;
import com.ll.rideon.global.monitoring.MetricsService;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RidingServiceTest {

    @Mock
    private RidingSessionRepository ridingSessionRepository;

    @Mock
    private RidingLocationRepository ridingLocationRepository;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private RidingService ridingService;

    private RidingSession mockRidingSession;

    @BeforeEach
    void setUp() {
        mockRidingSession = RidingSession.builder()
                .userId(1L)
                .build();
    }

    @Test
    void createRidingSession_Success() {
        // given
        Long userId = 1L;
        RidingSessionCreateRequestDto requestDto = new RidingSessionCreateRequestDto();
        
        when(ridingSessionRepository.findActiveSessionByUserId(userId))
                .thenReturn(Optional.empty());
        when(ridingSessionRepository.save(any(RidingSession.class)))
                .thenReturn(mockRidingSession);
        when(metricsService.startRidingSessionTimer())
                .thenReturn(mock(Timer.Sample.class));

        // when
        var result = ridingService.createRidingSession(userId, requestDto);

        // then
        assertThat(result).isNotNull();
        verify(ridingSessionRepository).findActiveSessionByUserId(userId);
        verify(ridingSessionRepository).save(any(RidingSession.class));
    }

    @Test
    void createRidingSession_AlreadyActiveSession_ThrowsException() {
        // given
        Long userId = 1L;
        RidingSessionCreateRequestDto requestDto = new RidingSessionCreateRequestDto();
        
        when(ridingSessionRepository.findActiveSessionByUserId(userId))
                .thenReturn(Optional.of(mockRidingSession));

        // when & then
        assertThatThrownBy(() -> ridingService.createRidingSession(userId, requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 진행 중인 라이딩 세션이 있습니다.");
    }

    @Test
    void endRidingSession_Success() {
        // given
        Long sessionId = 1L;
        when(ridingSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(mockRidingSession));
        when(ridingLocationRepository.findOfflineSyncLocationsBySessionId(sessionId))
                .thenReturn(java.util.Collections.emptyList());

        // when
        ridingService.endRidingSession(sessionId);

        // then
        assertThat(mockRidingSession.getStatus()).isEqualTo(RidingStatus.COMPLETED);
        assertThat(mockRidingSession.getEndedAt()).isNotNull();
        verify(ridingSessionRepository).findById(sessionId);
    }

    @Test
    void endRidingSession_SessionNotFound_ThrowsException() {
        // given
        Long sessionId = 1L;
        when(ridingSessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ridingService.endRidingSession(sessionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라이딩 세션을 찾을 수 없습니다.");
    }
}
