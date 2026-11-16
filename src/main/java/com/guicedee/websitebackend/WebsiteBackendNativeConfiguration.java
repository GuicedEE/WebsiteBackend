package com.guicedee.websitebackend;

import com.guicedee.nativeimage.GuiceNativeConfiguration;
import com.guicedee.nativeimage.GuiceNativeRegistration;
import com.guicedee.websitebackend.dto.*;
import com.guicedee.websitebackend.health.*;
import com.guicedee.websitebackend.registry.ServiceRegistryPackageClass;
import com.guicedee.websitebackend.auth.AuthPackageClass;
import com.guicedee.websitebackend.resources.*;

/** Metadata only: no injector, credentials, or network clients are created at image build time. */
public final class WebsiteBackendNativeConfiguration implements GuiceNativeConfiguration {
    @Override public void configure(GuiceNativeRegistration registration) {
        registration.reflect(Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
                Boot.class, BackendLivenessCheck.class, ServiceRegistryReadinessCheck.class,
                ServiceRegistryPackageClass.class, AuthPackageClass.class,
                InternalResource.class, ServicesResource.class, StatusResource.class,
                AggregatedStatusDTO.class, HealthDetailDTO.class, ServiceStatusDTO.class);
        try {
            var loader = getClass().getClassLoader();
            for (String type : new String[]{"com.google.inject.name.Named", "com.google.inject.name.NamedImpl", "jakarta.inject.Named",
                    "com.guicedee.guicedservlets.openapi.implementations.OpenAPIProvider", "com.guicedee.vertx.web.spi.ManagedHttpListenerGroup", "com.guicedee.rest.pathing.OperationRegistry",
                    "com.guicedee.client.scopes.CallScopeProperties",
                    "com.guicedee.client.scopes.CallScoper", "com.guicedee.client.utils.GlobalProperties",
                    "com.guicedee.client.implementations.GuicedEEClientPostStartup",
                    "com.guicedee.vertx.spi.VertxVerticlePostStartup"}) {
                registration.reflect(Class.forName(type, false, loader));
            }
            registration.inspectMemberInjection(Class.forName("io.vertx.core.impl.VertxImpl", false, loader));
        } catch (ClassNotFoundException e) { throw new IllegalStateException("Missing native bootstrap dependency", e); }
    }
}



