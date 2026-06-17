# ByteFood

A SaaS-style restaurant management system consisting of a REST API backend, a web-based admin panel, and a JavaFX desktop POS client.

---

## System Architecture

ByteFood manages restaurant operations across two clients — an admin panel for menu and inventory management, and a desktop POS for front-of-house staff — both backed by a single REST API.

```
Admin Panel (React)
        │
        ▼
   REST API (Spring Boot) ──── PostgreSQL
        │         │
        │    [Outbox table]
        │         │
        │    KafkaScheduler (polling)
        │         │
        │         ▼
        │      Kafka
        │         │
        │         ▼
        │   StockEventListener
        │         │
        │         ▼
        │    Stock update ──── PostgreSQL
        │
POS Client (JavaFX)
        │
        ├── Device auth check ──── Redis
        │
        └── POST /orders ──────── REST API (same path as above)
```

---

## Architectural Decisions

### Kafka for stock updates (Outbox pattern)

Stock deduction runs outside the order transaction. When an order is persisted, a corresponding `OutboxStockEvent` row is written in the same transaction. A scheduler (`KafkaScheduler`) polls for unpublished entries and publishes them to a Kafka topic; `StockEventListener` consumes that topic and applies the deduction. The order response is not blocked by inventory writes, and if Kafka is temporarily unavailable, events accumulate in the outbox and are published once connectivity is restored.

The alternative was Spring `@Async`. Kafka was chosen for its durable message log — if the application crashes mid-publish, unprocessed outbox rows remain and will be retried, which is not guaranteed with in-process async execution.

### Redis for POS device authorization

The POS client runs on hardware inside the venue and communicates with the API over the local network or internet. Without device-level authorization, any HTTP client that obtained a valid JWT could submit orders.

Each POS device is registered in advance and assigned a session token stored in Redis. Every order request is validated against this token. Redis is used rather than a database table because:

- Token lookups need to be fast and happen on every order request.
- TTL-based expiration is a native Redis feature, removing the need for a scheduled cleanup job.
- The token store is logically separate from the main application database, making it straightforward to revoke all POS sessions independently of user accounts.

---

## Tech Stack

**Backend**
Java
Spring Boot
Hibernate
PostgreSQL
Kafka
Redis

**Frontend**  
React
Redux Toolkit
React Query

**POS**
JavaFX

**Infrastructure**
Docker
Traefik
GitHub Actions
Watchtower
Infisical

**Observability**
Prometheus
Grafana
Loki
Jaeger

---

## Features

- Redis-based device authorization for POS clients
- Inventory module: stock levels updated asynchronously after each order via Kafka 
- Place and manage orders from the POS client (combos, modifications, device authorization)
- Manage menu: semi-products, products, combos, subcategories, modification templates
- JWT authentication for admin users 
- Onboarding flow for initial restaurant setup

---

## Project Structure

```
.
├── backend/                    # Spring Boot application
│   └── src/main/java/org/example/
│       ├── controllers/        # REST controllers (one per domain entity)
│       ├── dtos/               # Request/response DTOs
│       ├── kafka/              # KafkaConfig, KafkaScheduler (outbox poller), StockEventListener
│       ├── models/             # JPA entities
│       ├── repositories/       # Spring Data JPA repositories
│       ├── security/           # JWT filter, security config, Redis device auth
│       ├── services/           # Business logic
│       └── config/             # App-level config beans
│
├── frontend/                   # React admin panel
│   └── src/
│       ├── api/                # Axios client and endpoint definitions
│       ├── components/         # Reusable UI components
│       ├── pages/              # Route-level page components
│       ├── store/              # Redux slices
│       └── hooks/              # Custom React hooks
│
├── pos/                        # JavaFX desktop POS client
│   └── src/main/java/org/example/posFX/
│       ├── auth/               # Device authorization flow
│       ├── session/            # Session token management
│       ├── navigation/         # Screen/view navigation
│       ├── apiCommunication/   # HTTP client for backend API
│       └── objects/            # Local model classes
│
├── grafana-dashboards/         # Pre-provisioned Grafana dashboard JSONs
├── docker-compose.yml          # Production deployment
├── docker-compose.local.yml    # Local development
├── prometheus.yml              # Prometheus scrape config
├── loki-config.yaml            # Loki storage config
├── datasources.yml             # Grafana datasource provisioning
├── dashboards.yml              # Grafana dashboard provisioning
└── traefik.yml                 # Traefik static config
```

---

## Requirements

- Docker 24+ and Docker Compose v2
- Java 21+ and Maven 3.8+ (to run backend or POS outside Docker)
- Node.js 20+ and npm (to run frontend outside Docker)

Secrets and environment-specific configuration are managed via [Infisical](https://infisical.com) and injected at deploy time.

---

## CI/CD

Two path-filtered GitHub Actions workflows run on push to `main`. Changes in `backend/**` trigger a test job followed by a Docker image build and push to GHCR — the build only runs if tests pass. Changes in `frontend/**` trigger a build-and-push directly, with no test stage. Both workflows can also be triggered manually.

Watchtower runs on the server, polls GHCR every 30 seconds, and restarts containers when a new `:latest` image is available. The backend uses a rolling restart so there is no downtime during redeploy.

---

## Project Status

**Active development.**

Backend, admin panel, POS client, and observability stack are production-ready and running at [bytefood.pl](https://bytefood.pl).

Planned:

- Order history and reporting views in the admin panel
- Multi-restaurant support
- POS: table assignment and split-bill handling

---

## Author

Wiktor — [GitHub](https://github.com/acafax)