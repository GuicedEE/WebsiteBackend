package com.guicedee.websitebackend.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;

/**
 * DTO representing the health/status of a single registered service.
 */
@Data
@Accessors(chain = true)
public class ServiceStatusDTO {
    private String name;
    private String status;
    private String url;
    private String externalUrl;
    private String healthPath;
    private Instant lastChecked;
    private List<String> aliases;
    private List<HealthDetailDTO> healthDetails;
}

