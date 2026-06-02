package com.guicedee.websitebackend.routes;

import com.guicedee.vertx.web.spi.VertxRouterConfigurator;
import io.vertx.ext.web.Router;

import java.util.Set;

/**
 * Adds CORS headers to allow public website frontends to call the status API.
 * Internal endpoints (/api/internal/**) are not CORS-enabled — they require JWT.
 */
public class CorsRouteConfigurator implements VertxRouterConfigurator<CorsRouteConfigurator> {

    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "https://guicedee.com",
            "https://jwebmp.com",
            "https://jwebswing.com",
            "https://activity-master.com",
            "https://entityassist.com",
            "http://localhost:4200",
            "http://localhost:8765"
    );

    @Override
    public Router builder(Router router) {
        router.route("/api/status*").handler(ctx -> {
            String origin = ctx.request().getHeader("Origin");
            if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
                ctx.response()
                        .putHeader("Access-Control-Allow-Origin", origin)
                        .putHeader("Access-Control-Allow-Methods", "GET, OPTIONS")
                        .putHeader("Access-Control-Allow-Headers", "Content-Type, Accept")
                        .putHeader("Access-Control-Max-Age", "3600");
            }
            if ("OPTIONS".equalsIgnoreCase(ctx.request().method().name())) {
                ctx.response().setStatusCode(204).end();
            } else {
                ctx.next();
            }
        });

        router.route("/api/services*").handler(ctx -> {
            String origin = ctx.request().getHeader("Origin");
            if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
                ctx.response()
                        .putHeader("Access-Control-Allow-Origin", origin)
                        .putHeader("Access-Control-Allow-Methods", "GET, OPTIONS")
                        .putHeader("Access-Control-Allow-Headers", "Content-Type, Accept")
                        .putHeader("Access-Control-Max-Age", "3600");
            }
            if ("OPTIONS".equalsIgnoreCase(ctx.request().method().name())) {
                ctx.response().setStatusCode(204).end();
            } else {
                ctx.next();
            }
        });

        return router;
    }

    @Override
    public Integer sortOrder() {
        return 100; // Run early, before REST routes
    }
}

