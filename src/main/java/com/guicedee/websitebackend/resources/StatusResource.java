package com.guicedee.websitebackend.resources;

import com.guicedee.service.registry.ServiceEntry;
import com.guicedee.service.registry.ServiceRegistry;
import com.guicedee.websitebackend.dto.AggregatedStatusDTO;
import com.guicedee.websitebackend.dto.HealthDetailDTO;
import com.guicedee.websitebackend.dto.ServiceStatusDTO;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Public REST resource — aggregated service status for website frontends.
 * CORS-enabled via {@link com.guicedee.websitebackend.routes.CorsRouteConfigurator}.
 */
@Path("/api/status")
@Tag(name = "Status", description = "Public aggregated health status for all registered services")
@Produces(MediaType.APPLICATION_JSON)
@Log4j2
public class StatusResource {

    @GET
    @Operation(summary = "Get aggregated status of all registered services")
    public Uni<AggregatedStatusDTO> getStatus() {
        return Uni.createFrom().item(() -> {
            var entries = ServiceRegistry.all();
            List<ServiceStatusDTO> services = new ArrayList<>();
            int up = 0, degraded = 0, down = 0;

            for (ServiceEntry entry : entries.values()) {
                var dto = new ServiceStatusDTO()
                        .setName(entry.name())
                        .setStatus(entry.status().name())
                        .setUrl(entry.url())
                        .setExternalUrl(entry.externalUrl())
                        .setHealthPath(entry.healthPath())
                        .setLastChecked(entry.lastChecked())
                        .setAliases(List.of())
                        .setHealthDetails(entry.healthDetails().stream()
                                .map(d -> new HealthDetailDTO()
                                        .setName(d.name())
                                        .setStatus(d.status().name())
                                        .setData(d.data()))
                                .toList());

                services.add(dto);

                switch (entry.status()) {
                    case UP -> up++;
                    case DEGRADED -> degraded++;
                    case DOWN -> down++;
                    default -> { /* UNKNOWN */ }
                }
            }

            String overall = (down > 0) ? "DEGRADED" : (degraded > 0) ? "DEGRADED" : "UP";

            return new AggregatedStatusDTO()
                    .setOverallStatus(overall)
                    .setTimestamp(Instant.now())
                    .setTotalServices(entries.size())
                    .setHealthyServices(up)
                    .setDegradedServices(degraded)
                    .setDownServices(down)
                    .setServices(services);
        });
    }
}

