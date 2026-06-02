package com.guicedee.websitebackend.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;

/**
 * Aggregated status response — all registered services and overall health.
 */
@Data
@Accessors(chain = true)
public class AggregatedStatusDTO {
    private String overallStatus;
    private Instant timestamp;
    private int totalServices;
    private int healthyServices;
    private int degradedServices;
    private int downServices;
    private List<ServiceStatusDTO> services;
}

