package com.guicedee.websitebackend.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.Map;

@Data
@Accessors(chain = true)
public class HealthDetailDTO {
    private String name;
    private String status;
    private Map<String, Object> data;
}
