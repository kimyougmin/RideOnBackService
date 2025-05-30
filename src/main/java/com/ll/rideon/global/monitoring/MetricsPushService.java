package com.ll.rideon.global.monitoring;

import com.ll.rideon.domain.statistice.UserStatsService;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MetricsPushService {

    private final UserStatsService statsService;
    private final CollectorRegistry registry;

    private final Gauge dauGauge;
    private final Gauge mauGauge;

    public MetricsPushService(UserStatsService statsService, CollectorRegistry registry) {
        this.statsService = statsService;
        this.registry = registry;

        dauGauge = Gauge.build()
                .name("rideon_dau")
                .help("Daily Active Users")
                .register(registry);

        mauGauge = Gauge.build()
                .name("rideon_mau")
                .help("Monthly Active Users")
                .register(registry);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void pushMetrics() {
        dauGauge.set(statsService.getDailyActiveUserCount());
        mauGauge.set(statsService.getMonthlyActiveUserCount());

        try {
            PushGateway pushGateway = new PushGateway("localhost:9091");
            pushGateway.pushAdd(registry, "rideon_usage");
            System.out.println("✅ Metrics pushed to PushGateway");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
