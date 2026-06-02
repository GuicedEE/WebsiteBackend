package com.guicedee.websitebackend.resources;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * Internal-only REST resource — secured with JWT.
 * Only accessible by backend services with the "internal-service" role.
 */
@Path("/api/internal")
@Tag(name = "Internal", description = "Internal service-to-service endpoints (JWT required)")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("internal-service")
@Log4j2
public class InternalResource {

    @GET
    @Path("/config")
    @Operation(summary = "Retrieve backend configuration (internal only)")
    public Uni<Map<String, Object>> getConfig() {
        return Uni.createFrom().item(() -> Map.of(
                "serviceName", "website-backend",
                "version", "2.0.2",
                "environment", System.getenv().getOrDefault("ENVIRONMENT", "development")
        ));
    }
}

