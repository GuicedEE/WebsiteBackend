@ServiceRegistryOptions(
        healthCheckInterval = 12 * 60 * 60, // seconds; let idle websites scale down between checks
        healthPath = "/health/ready",
        registerSelf = true,
        useHttps = true
)
@RegisteredService(
        name = "guicedee-website",
        aliases = {"guicedee"},
        externalUrls = {"https://guicedee.com"},
        healthPath = "/index.html",
        expectedContentType = "text/html"
)
@RegisteredService(
        name = "jwebmp-website",
        aliases = {"jwebmp", "jwebswing"},
        externalUrls = {"https://jwebmp.com", "https://jwebswing.com"},
        healthPath = "/index.html",
        expectedContentType = "text/html"
)
@RegisteredService(
        name = "activity-master-website",
        aliases = {"activity-master", "activitymaster"},
        externalUrls = {"https://activity-master.com"},
        healthPath = "/",
        expectedContentType = "text/html"
)
@RegisteredService(
        name = "entity-assist-website",
        aliases = {"entity-assist", "entityassist"},
        externalUrls = {"https://entityassist.com"},
        healthPath = "/",
        expectedContentType = "text/html"
)
@RegisteredService(
        name = "activity-master-api",
        aliases = {"am-api"},
        url = "${ACTIVITY_MASTER_API_URL}",
        healthPath = "/health/ready"
)
package com.guicedee.websitebackend.registry;

import com.guicedee.service.registry.RegisteredService;
import com.guicedee.service.registry.ServiceRegistryOptions;
