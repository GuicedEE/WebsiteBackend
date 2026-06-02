package com.guicedee.websitebackend.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Liveness check — always UP if the JVM is running.
 */
@Liveness
public class BackendLivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("backend-liveness")
                .up()
                .withData("service", "website-backend")
                .build();
    }
}

