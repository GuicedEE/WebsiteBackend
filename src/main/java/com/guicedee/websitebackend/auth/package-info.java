@JwtAuthOptions(
        keystorePath = "${JWT_KEYSTORE_PATH}",
        keystoreType = "pkcs12",
        keystorePassword = "${JWT_KEYSTORE_PASSWORD}",
        algorithm = "RS256",
        issuer = "guicedee-backend",
        audience = {"guicedee-website", "jwebmp-website", "activity-master-website", "entity-assist-website"},
        permissionsClaimKey = "realm_access/roles"
)
package com.guicedee.websitebackend.auth;

import com.guicedee.vertx.auth.jwt.JwtAuthOptions;

