import com.guicedee.websitebackend.Boot;
import com.guicedee.websitebackend.guice.WebsiteBackendModule;
import com.guicedee.websitebackend.routes.CorsRouteConfigurator;

module com.guicedee.websitebackend {
    // GuicedEE core
    requires com.guicedee.client;
    requires com.guicedee.guicedinjection;

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

    // Lombok
    requires static lombok;

    // Guice SPI registrations
    provides com.guicedee.client.services.lifecycle.IGuiceModule
            with WebsiteBackendModule;

    provides com.guicedee.vertx.web.spi.VertxRouterConfigurator
            with CorsRouteConfigurator;

    opens com.guicedee.websitebackend to com.fasterxml.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.dto to com.fasterxml.jackson.databind, com.google.guice;
    //opens com.guicedee.websitebackend.auth to  com.fasterxml.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.health to com.fasterxml.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.guice to com.fasterxml.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.routes to com.fasterxml.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.registry to com.fasterxml.jackson.databind, com.google.guice;
    opens com.guicedee.websitebackend.resources to com.fasterxml.jackson.databind, com.google.guice, com.guicedee.rest;
}

