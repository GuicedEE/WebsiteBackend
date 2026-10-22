import com.guicedee.websitebackend.Boot;
import com.guicedee.websitebackend.guice.WebsiteBackendModule;
import com.guicedee.websitebackend.routes.CorsRouteConfigurator;

module com.guicedee.websitebackend {
    // GuicedEE core
    requires com.guicedee.client;
    requires com.guicedee.guicedinjection;
    requires com.guicedee.telemetry;

    // Vert.x web + REST
    requires com.guicedee.vertx.web;
    requires com.guicedee.rest;
    requires com.guicedee.rest.client;

    // Service registry
    requires com.guicedee.service.registry;

    // Health
    requires com.guicedee.health;

    // OpenAPI + Swagger UI
    requires com.guicedee.openapi;
    requires com.guicedee.swaggerui;

    // JWT auth
    requires io.vertx.auth.jwt;

    // Logging
    requires org.apache.logging.log4j.core;
    // Cloud JSON logging is supplied by the client module without Jackson 2.

    // Lombok
    requires static lombok;

    // Guice SPI registrations
    provides com.guicedee.client.services.lifecycle.IGuiceModule
            with WebsiteBackendModule;

    provides com.guicedee.vertx.web.spi.VertxRouterConfigurator
            with CorsRouteConfigurator;

    opens com.guicedee.websitebackend to tools.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.dto to tools.jackson.databind, com.google.guice;
    //opens com.guicedee.websitebackend.auth to  tools.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.health to tools.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.guice to tools.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.routes to tools.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.registry to tools.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.resources to tools.jackson.databind, com.google.guice, com.guicedee.rest;
}

