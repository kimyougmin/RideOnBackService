package com.ll.rideon.global.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsConfig implements CommandLineRunner {

    private final MetricsService metricsService;

    @Override
    public void run(String... args) throws Exception {
        log.info("프로메테우스 메트릭 초기화를 시작합니다...");
        metricsService.initializeMetrics();
        log.info("프로메테우스 메트릭 초기화가 완료되었습니다.");
    }
} 