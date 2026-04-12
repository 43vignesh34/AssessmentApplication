package com.example.assessmentapplication.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * A beginner-friendly example of a custom Health Indicator.
 * This will show up in /actuator/health under the 'customApp' key.
 */
@Component
public class CustomAppHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // In a real app, you might check if a cache is warm,
        // if a file exists, or if a legacy system is reachable.
        boolean isReady = checkInternalLogic();

        if (isReady) {
            return Health.up()
                    .withDetail("Message", "The application service is healthy and ready to serve!")
                    .withDetail("ReadyTimestamp", System.currentTimeMillis())
                    .build();
        } else {
            return Health.down()
                    .withDetail("Error", "The internal logic is failing. Check logs.")
                    .build();
        }
    }

    private boolean checkInternalLogic() {
        // Simulating a successful check
        return true;
    }
}
