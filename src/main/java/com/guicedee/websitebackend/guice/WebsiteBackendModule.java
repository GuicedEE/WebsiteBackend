package com.guicedee.websitebackend.guice;

import com.google.inject.AbstractModule;
import com.guicedee.client.services.lifecycle.IGuiceModule;

/**
 * Guice module for the Website Backend — binds services and configuration.
 */
public class WebsiteBackendModule extends AbstractModule implements IGuiceModule<WebsiteBackendModule> {

    @Override
    protected void configure() {
        // Bindings are handled by annotation scanning (service registry, health, REST).
        // Add custom bindings here as needed.
    }

    @Override
    public Integer sortOrder() {
        return Integer.MIN_VALUE + 200;
    }
}

