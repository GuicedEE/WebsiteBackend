package com.guicedee.websitebackend.resources;

import com.guicedee.service.registry.ServiceEntry;
import com.guicedee.service.registry.ServiceRegistry;
import com.guicedee.websitebackend.dto.ServiceStatusDTO;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * Public REST resource — service directory for website frontends.
 */
@Path("/api/services")
@Tag(name = "Services", description = "Service directory and lookup")
@Produces(MediaType.APPLICATION_JSON)
@Log4j2
public class ServicesResource {

    @GET
    @Operation(summary = "List all registered services")
    public Uni<List<ServiceStatusDTO>> listServices() {
        return Uni.createFrom().item(() ->
                ServiceRegistry.all().values().stream()
                        .map(this::toDto)
                        .toList()
        );
    }

    @GET
    @Path("/{name}")
    @Operation(summary = "Get a specific service by name or alias")
    public Uni<ServiceStatusDTO> getService(@PathParam("name") String name) {
        return Uni.createFrom().item(() -> {
            ServiceEntry entry = ServiceRegistry.get(name)
                    .orElseThrow(() -> new jakarta.ws.rs.NotFoundException("Service not found: " + name));
            return toDto(entry);
        });
    }

    private ServiceStatusDTO toDto(ServiceEntry entry) {
        return new ServiceStatusDTO()
                .setName(entry.name())
                .setStatus(entry.status().name())
                .setUrl(entry.url())
                .setExternalUrl(entry.externalUrl())
                .setHealthPath(entry.healthPath())
                .setLastChecked(entry.lastChecked());
    }
}

