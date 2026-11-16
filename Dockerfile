# The context is prepared by build-native.ps1, not the repository root.
FROM quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-25@sha256:c0ff31764f70469795db5513d674e4721fd8e7e761beddbff741ef5d84e79ed3 AS native-build
USER root
WORKDIR /src
COPY NativeBuild.java application.jar ./
COPY native-libs/ native-libs/
RUN java NativeBuild.java application.jar native-libs com.guicedee.websitebackend/com.guicedee.websitebackend.Boot \
    && native-image -J-Xmx8g @target/native-image.args

# Mandrel emits a glibc binary; a musl-only Alpine base cannot run it.
FROM debian:trixie-slim@sha256:a72b4433de1cf7d6bae0c4cee74d0547f58601a643b3375f070c51600d63b6b2
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates zlib1g \
    && rm -rf /var/lib/apt/lists/* && mkdir -p /app/logs && chown -R 65532:65532 /app
WORKDIR /app
COPY --from=native-build --chown=65532:65532 /src/target/native/application /app/application
USER 65532:65532
ENV GUICEDEE_DISCOVERY_MODE=spi
EXPOSE 8080
ENTRYPOINT ["/app/application"]
