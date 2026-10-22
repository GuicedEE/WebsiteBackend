# GuicedEE Website Backend

Backend aggregator service for the GuicedEE ecosystem websites — provides a unified service registry, health status aggregation, OpenAPI documentation, and JWT-secured internal APIs.

## Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│ Azure Container Apps Environment                                    │
│                                                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────────┐   │
│  │ guicedee-    │  │ jwebmp-      │  │ website-backend         │   │
│  │ website      │  │ website      │  │ (this service)          │   │
│  │ (Angular SPA)│  │ (Angular SPA)│  │                         │   │
│  │              │  │              │  │ • ServiceRegistry       │   │
│  │ /health/ready│  │ /health/ready│  │ • Polls all services    │   │
│  └──────┬───────┘  └──────┬───────┘  │ • REST: /api/status     │   │
│         │                  │          │ • REST: /api/services   │   │
│         │                  │          │ • /health/ready         │   │
│         └──────────────────┴──────────┤ • /openapi.json         │   │
│                                       │ • /swagger/             │   │
│  ┌──────────────┐  ┌──────────────┐  │                         │   │
│  │ activity-    │  │ entity-      │  │ Internal (JWT):         │   │
│  │ master       │  │ assist       │  │ • /api/internal/config  │   │
│  │ (internal)   │  │ website      │  └────────────────────────┘   │
│  └──────────────┘  └──────────────┘                                │
│                                                                      │
│  Frontend sites call: website-backend/api/status (public, CORS)      │
│  Backend services call: /api/internal/* (JWT required)               │
└────────────────────────────────────────────────────────────────────┘
```

## Registered Services

| Service | External URL | Type |
|---|---|---|
| `guicedee-website` | https://guicedee.com | Public frontend |
| `jwebmp-website` | https://jwebmp.com | Public frontend |
| `activity-master-website` | https://activity-master.com | Public frontend |
| `entity-assist-website` | https://entityassist.com | Public frontend |
| `activity-master-api` | (internal) | Internal API |
| `payment-service` | (internal) | Internal API |

## Public Endpoints (CORS-enabled)

### Website polling

The backend checks registered websites once at startup, then every **12 hours**
(`43,200` seconds), configured by `@ServiceRegistryOptions.healthCheckInterval` in
`src/main/java/com/guicedee/websitebackend/registry/package-info.java`.
`GET /api/status` and the backend readiness check read the cached registry state;
they do not trigger additional requests to the websites. Each service's `lastChecked`
records when its status was observed. Failed services may be checked less often due
to the registry's exponential backoff.

The interval applies while a backend instance is running. A restart or scale-from-zero
starts a new instance and performs an initial check. Frontend refresh timers are
independent: Activity Master refreshes every 12 hours and GuicedEE every 24 hours,
in addition to their initial status request when the page loads.

Deployed on 2026-09-12 in `Pay-As-You-Go / DevSites`: backend image
`gedmarc/guicedee-website-backend:2026-09-12-polling2` and frontend image
`gedmarc/activity-master-website:2026-09-12-polling`. The previous live Activity Master
bundle refreshed status every 30 seconds. Browser clock checks of the replacement
verify the initial request and exactly one refresh in each of two simulated 12-hour
cycles. The backend's cloud-mode startup reports `43200s`; status calls return cached
check timestamps. Only the new backend revision receives traffic and remains active.

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/status` | Aggregated health status of all services |
| `GET` | `/api/services` | List all registered services |
| `GET` | `/api/services/{name}` | Lookup a specific service |
| `GET` | `/health/ready` | Readiness probe |
| `GET` | `/health/live` | Liveness probe |
| `GET` | `/openapi.json` | OpenAPI 3.1 spec |
| `GET` | `/swagger/` | Swagger UI |

## Internal Endpoints (JWT required, role: `internal-service`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/internal/config` | Backend configuration |

## Local Development

```bash
# Copy environment template
cp .env.example .env

# Build
mvn package

# Run
java -cp "target/website-backend-2.4.1.jar:target/dependency/*" \
    com.guicedee.websitebackend.Boot
```

## Deployment (Azure Container Apps)

### GuicedEE 2.4.1 deployment - 16 October 2026

- Revision: `website-backend--v241-20261016`, active with 100% of traffic.
- Immutable image: `docker.io/gedmarc/guicedee-website-backend@sha256:5b71c8f532dafafd6a8e0f56427d7843a45d323da12d3180f6399a697dc15397` (tag `2.4.1-20261016`).
- The Container App was explicitly stopped before this deployment; it is now running.
  The previous revision `website-backend--0000004` is deactivated.
- The Linux Java 25 image contains GuicedEE 2.4.1 and Jackson 3.2.3, with no Jackson 2
  core/databind. Cloud JSON startup, readiness/liveness, APIs, Swagger/OpenAPI,
  frontend CORS and denial of anonymous internal-config requests were verified.
- Boot awaits startup through `GuiceApplication.run` and relies on its console setup.
  Explicit telemetry options default to disabled; the deployment sets
  `TELEMETRY_ENABLED=false` because no collector is configured. Operators can enable
  telemetry explicitly with a configured collector.
- `SERVICE_REGISTRY_DNS_SUFFIX=whitegrass-2e17714c.ukwest.azurecontainerapps.io`
  supplies the deployment DNS fallback. EntityAssist is checked through its public
  `https://entityassist.com` URL, avoiding the mismatched Container App name.
- All four public websites are healthy. The optional ActivityMaster API remains
  unconfigured (`ACTIVITY_MASTER_API_URL`); its registry entry reports DOWN and the
  aggregate dashboard reports DEGRADED. Backend readiness is UP.

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars with your subscription ID

terraform init
terraform plan
terraform apply
```

## Security Model

- **Public endpoints** (`/api/status`, `/api/services`): CORS-restricted to known website origins, no authentication required
- **Internal endpoints** (`/api/internal/*`): JWT authentication with `@RolesAllowed("internal-service")`
- **JWT configuration**: RS256 with PKCS12 keystore, configurable via environment variables
