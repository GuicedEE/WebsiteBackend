package com.guicedee.websitebackend;

import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.GuiceApplication;
import com.guicedee.telemetry.TelemetryOptions;
import lombok.extern.log4j.Log4j2;

/**
 * Bootstrap entry point for the Website Backend aggregator service.
 * <p>
 * Starts the Vert.x HTTP server with:
 * <ul>
 *   <li>Service registry for all GuicedEE ecosystem websites</li>
 *   <li>REST API for aggregated health/status</li>
 *   <li>OpenAPI spec + Swagger UI</li>
 *   <li>JWT-secured internal endpoints</li>
 * </ul>
 */
@Log4j2
@TelemetryOptions(enabled = false, serviceName = "guicedee-website-backend", serviceVersion = "2.4.1", exportLogs = false)
public final class Boot {

    private Boot() {
    }

    public static void main(String[] args) {
        GuiceApplication.run(() -> IGuiceContext.registerModule("com.guicedee.websitebackend"));

        log.info("🚀 Website Backend aggregator service ready.");
    }
}

