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
mvn clean package

# Run
java -cp "target/website-backend-2.0.2.jar:target/dependency/*" \
    com.guicedee.websitebackend.Boot
```

## Deployment (Azure Container Apps)

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
