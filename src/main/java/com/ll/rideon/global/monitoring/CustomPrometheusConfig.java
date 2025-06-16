package com.ll.rideon.global.monitoring;

import io.prometheus.client.CollectorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomPrometheusConfig {

    @Bean
    public CollectorRegistry customCollectorRegistry() {
        return new CollectorRegistry();
    }
}