package com.guicedee.websitebackend;

import com.guicedee.client.services.IGuiceDiscovery;
import com.guicedee.websitebackend.health.*;
import com.guicedee.websitebackend.registry.ServiceRegistryPackageClass;
import com.guicedee.websitebackend.auth.AuthPackageClass;
import com.guicedee.websitebackend.resources.*;
import java.util.Collection;
import java.util.List;

/** Application classes and annotated packages for closed-world SPI discovery. */
public final class WebsiteBackendDiscovery implements IGuiceDiscovery<WebsiteBackendDiscovery> {
    @Override public Collection<Class<?>> classes() {
        return List.of(Boot.class, BackendLivenessCheck.class, ServiceRegistryReadinessCheck.class,
                ServiceRegistryPackageClass.class, AuthPackageClass.class,
                InternalResource.class, ServicesResource.class, StatusResource.class);
    }
}
