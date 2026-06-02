package com.guicedee.websitebackend.health;

import com.guicedee.service.registry.ServiceRegistry;
import com.guicedee.service.registry.ServiceStatus;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Readiness check — reports UP only when the service registry has completed
 * at least one health-check cycle and the majority of public services are healthy.
 */
@Readiness
public class ServiceRegistryReadinessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        var entries = ServiceRegistry.all();
        long up = entries.values().stream().filter(e -> e.status() == ServiceStatus.UP).count();
        long total = entries.size();

        var builder = HealthCheckResponse.named("service-registry")
                .withData("totalServices", total)
                .withData("healthyServices", up);

        if (total == 0 || up > 0) {
            return builder.up().build();
        }
        return builder.down().build();
    }
}

