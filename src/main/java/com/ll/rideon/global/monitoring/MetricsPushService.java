package com.ll.rideon.global.monitoring;

import com.ll.rideon.domain.statistice.UserStatsService;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;

@Service
public class MetricsPushService {

    private final UserStatsService statsService;
    private final CollectorRegistry collectorRegistry;

    private final Gauge dauGauge;
    private final Gauge mauGauge;

    public MetricsPushService(UserStatsService statsService,
                              @Qualifier("customCollectorRegistry") CollectorRegistry registry) {
        this.statsService = statsService;
        this.collectorRegistry = registry;

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
            pushGateway.pushAdd(collectorRegistry, "rideon_usage");
            System.out.println("Metrics pushed to PushGateway");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Dypendency Injection
    // Spring Bean
    // RideOnApplication ->

}
