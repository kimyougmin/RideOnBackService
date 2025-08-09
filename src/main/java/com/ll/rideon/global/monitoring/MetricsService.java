package com.ll.rideon.global.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // 게시글 관련 메트릭
    private Counter postCreatedCounter;
    private Counter postUpdatedCounter;
    private Counter postDeletedCounter;
    private Counter postViewedCounter;
    private Timer postCreationTimer;

    // 댓글 관련 메트릭
    private Counter commentCreatedCounter;
    private Counter commentUpdatedCounter;
    private Counter commentDeletedCounter;
    private Timer commentCreationTimer;

    // 라이딩 관련 메트릭
    private Counter ridingSessionCreatedCounter;
    private Counter ridingSessionCompletedCounter;
    private Counter locationUpdateCounter;
    private Timer ridingSessionTimer;
    private Timer locationUpdateTimer;

    // 네트워크 관련 메트릭
    private Counter networkDisconnectionCounter;
    private Counter networkQualityChangeCounter;

    // 사용자 관련 메트릭
    private Counter userLoginCounter;
    private Counter userRegistrationCounter;
    private Counter authenticationFailureCounter;

    // API 호출 메트릭
    private Timer apiResponseTimer;

    public void initializeMetrics() {
        // 게시글 메트릭 초기화
        postCreatedCounter = Counter.builder("rideon.posts.created")
                .description("생성된 게시글 수")
                .register(meterRegistry);

        postUpdatedCounter = Counter.builder("rideon.posts.updated")
                .description("수정된 게시글 수")
                .register(meterRegistry);

        postDeletedCounter = Counter.builder("rideon.posts.deleted")
                .description("삭제된 게시글 수")
                .register(meterRegistry);

        postViewedCounter = Counter.builder("rideon.posts.viewed")
                .description("조회된 게시글 수")
                .register(meterRegistry);

        postCreationTimer = Timer.builder("rideon.posts.creation.time")
                .description("게시글 생성 소요 시간")
                .register(meterRegistry);

        // 댓글 메트릭 초기화
        commentCreatedCounter = Counter.builder("rideon.comments.created")
                .description("생성된 댓글 수")
                .register(meterRegistry);

        commentUpdatedCounter = Counter.builder("rideon.comments.updated")
                .description("수정된 댓글 수")
                .register(meterRegistry);

        commentDeletedCounter = Counter.builder("rideon.comments.deleted")
                .description("삭제된 댓글 수")
                .register(meterRegistry);

        commentCreationTimer = Timer.builder("rideon.comments.creation.time")
                .description("댓글 생성 소요 시간")
                .register(meterRegistry);

        // 라이딩 메트릭 초기화
        ridingSessionCreatedCounter = Counter.builder("rideon.riding.sessions.created")
                .description("생성된 라이딩 세션 수")
                .register(meterRegistry);

        ridingSessionCompletedCounter = Counter.builder("rideon.riding.sessions.completed")
                .description("완료된 라이딩 세션 수")
                .register(meterRegistry);

        locationUpdateCounter = Counter.builder("rideon.riding.location.updates")
                .description("위치 업데이트 수")
                .register(meterRegistry);

        ridingSessionTimer = Timer.builder("rideon.riding.session.duration")
                .description("라이딩 세션 지속 시간")
                .register(meterRegistry);

        locationUpdateTimer = Timer.builder("rideon.riding.location.update.time")
                .description("위치 업데이트 소요 시간")
                .register(meterRegistry);

        // 네트워크 메트릭 초기화
        networkDisconnectionCounter = Counter.builder("rideon.network.disconnections")
                .description("네트워크 연결 끊김 횟수")
                .register(meterRegistry);

        networkQualityChangeCounter = Counter.builder("rideon.network.quality.changes")
                .description("네트워크 품질 변경 횟수")
                .register(meterRegistry);

        // 사용자 메트릭 초기화
        userLoginCounter = Counter.builder("rideon.users.login")
                .description("사용자 로그인 횟수")
                .register(meterRegistry);

        userRegistrationCounter = Counter.builder("rideon.users.registration")
                .description("사용자 가입 횟수")
                .register(meterRegistry);

        authenticationFailureCounter = Counter.builder("rideon.authentication.failures")
                .description("인증 실패 횟수")
                .register(meterRegistry);

        // API 메트릭 초기화
        apiResponseTimer = Timer.builder("rideon.api.response.time")
                .description("API 응답 시간")
                .register(meterRegistry);

        log.info("프로메테우스 메트릭이 초기화되었습니다.");
    }

    // 게시글 메트릭 메서드
    public void incrementPostCreated() {
        postCreatedCounter.increment();
        log.debug("게시글 생성 메트릭 증가");
    }

    public void incrementPostUpdated() {
        postUpdatedCounter.increment();
        log.debug("게시글 수정 메트릭 증가");
    }

    public void incrementPostDeleted() {
        postDeletedCounter.increment();
        log.debug("게시글 삭제 메트릭 증가");
    }

    public void incrementPostViewed() {
        postViewedCounter.increment();
        log.debug("게시글 조회 메트릭 증가");
    }

    public Timer.Sample startPostCreationTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopPostCreationTimer(Timer.Sample sample) {
        sample.stop(postCreationTimer);
        log.debug("게시글 생성 타이머 종료");
    }

    // 댓글 메트릭 메서드
    public void incrementCommentCreated() {
        commentCreatedCounter.increment();
        log.debug("댓글 생성 메트릭 증가");
    }

    public void incrementCommentUpdated() {
        commentUpdatedCounter.increment();
        log.debug("댓글 수정 메트릭 증가");
    }

    public void incrementCommentDeleted() {
        commentDeletedCounter.increment();
        log.debug("댓글 삭제 메트릭 증가");
    }

    public Timer.Sample startCommentCreationTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopCommentCreationTimer(Timer.Sample sample) {
        sample.stop(commentCreationTimer);
        log.debug("댓글 생성 타이머 종료");
    }

    // 라이딩 메트릭 메서드
    public void incrementRidingSessionCreated() {
        ridingSessionCreatedCounter.increment();
        log.debug("라이딩 세션 생성 메트릭 증가");
    }

    public void incrementRidingSessionCompleted() {
        ridingSessionCompletedCounter.increment();
        log.debug("라이딩 세션 완료 메트릭 증가");
    }

    public void incrementLocationUpdate() {
        locationUpdateCounter.increment();
        log.debug("위치 업데이트 메트릭 증가");
    }

    public Timer.Sample startRidingSessionTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopRidingSessionTimer(Timer.Sample sample) {
        sample.stop(ridingSessionTimer);
        log.debug("라이딩 세션 타이머 종료");
    }

    public Timer.Sample startLocationUpdateTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopLocationUpdateTimer(Timer.Sample sample) {
        sample.stop(locationUpdateTimer);
        log.debug("위치 업데이트 타이머 종료");
    }

    // 네트워크 메트릭 메서드
    public void incrementNetworkDisconnection() {
        networkDisconnectionCounter.increment();
        log.debug("네트워크 연결 끊김 메트릭 증가");
    }

    public void incrementNetworkQualityChange() {
        networkQualityChangeCounter.increment();
        log.debug("네트워크 품질 변경 메트릭 증가");
    }

    // 사용자 메트릭 메서드
    public void incrementUserLogin() {
        userLoginCounter.increment();
        log.debug("사용자 로그인 메트릭 증가");
    }

    public void incrementUserRegistration() {
        userRegistrationCounter.increment();
        log.debug("사용자 가입 메트릭 증가");
    }

    public void incrementAuthenticationFailure() {
        authenticationFailureCounter.increment();
        log.debug("인증 실패 메트릭 증가");
    }

    // API 메트릭 메서드
    public Timer.Sample startApiResponseTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopApiResponseTimer(Timer.Sample sample) {
        sample.stop(apiResponseTimer);
        log.debug("API 응답 타이머 종료");
    }

    // 커스텀 게이지 메트릭
    public void recordActiveUsers(int count) {
        meterRegistry.gauge("rideon.users.active", count);
        log.debug("활성 사용자 수 기록: {}", count);
    }

    public void recordActiveRidingSessions(int count) {
        meterRegistry.gauge("rideon.riding.sessions.active", count);
        log.debug("활성 라이딩 세션 수 기록: {}", count);
    }

    public void recordDatabaseConnections(int count) {
        meterRegistry.gauge("rideon.database.connections", count);
        log.debug("데이터베이스 연결 수 기록: {}", count);
    }
} 