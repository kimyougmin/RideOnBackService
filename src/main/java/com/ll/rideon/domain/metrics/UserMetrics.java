package com.ll.rideon.domain.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMetrics {
    private final MeterRegistry meterRegistry;

    public UserMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordUserRequest(String userId) {
        boolean isLoggedIn = (userId != null && !userId.isEmpty());
        String status = isLoggedIn ? "logged_in" : "anonymous";
        meterRegistry.counter("rideon.user.request", "status", status).increment();
    }
}
