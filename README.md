# Gym CRM System

A Spring Boot REST API for a Gym CRM (Customer Relationship Management) system. It exposes **Trainer**, **Trainee**, and **Training** management as a `RestController`-based JSON API, packaged as an executable jar with an embedded servlet container, and persisted with **Spring Data JPA / Hibernate** on top of **PostgreSQL**.

---

## Features

- **18 REST endpoints** covering trainee/trainer registration, login/logout, password change, profile management, trainer-assignment, training scheduling, and training-type lookup (full list below)
- **Automatic username generation** – `firstName.lastName`, with a numeric suffix on duplicates (e.g. `John.Smith`, `John.Smith1`), unique across trainees and trainers alike (backed by the shared `users` table), which also makes it impossible to register the same identity as both a trainee and a trainer
- **Automatic password generation** – random 10-character string, stored hashed with BCrypt
- **JWT authentication** – `POST /api/login` verifies credentials via Spring Security's `AuthenticationManager` and issues a bearer JWT (`JwtService`); every protected endpoint is secured by a stateless `JwtAuthenticationFilter`, with `POST /api/logout` blacklisting the token's JTI so it can't be reused before it expires (`TokenBlacklistService`)
- **Login-attempt lockout** – `LoginAttemptService` locks an account for 5 minutes after 3 consecutive failed login attempts (`423 Locked`)
- **Persistence with Spring Data JPA** – entities mapped with JPA annotations, managed via `JpaRepository`-based repositories (`TraineeRepository`, `TrainerRepository`, `TrainingRepository`, `TrainingTypeRepository`, `UserRepository`); hard delete of a trainee cascades to their trainings
- **Training type seeding** – default training types are seeded on startup if the table is empty (`TrainingTypeSeeder`), and are read-only from the API
- **Request validation** – Jakarta Bean Validation on every request DTO, with a `@RestControllerAdvice` (`GlobalExceptionHandler`) translating validation/auth/not-found/lockout errors into structured JSON error responses
- **Two-level logging** – a servlet `Filter` (`TransactionIdFilter`) assigns/propagates a transaction id (via MDC + response header) across each request, and a `HandlerInterceptor` (`RestLoggingInterceptor`) logs each REST call's method, URI, status, and duration
- **API documentation** – springdoc-openapi generates OpenAPI 3 docs and serves Swagger UI directly from the Spring MVC controllers
- **Environment profiles** – `local`, `dev`, `stg`, `prod` Spring profiles, each with its own database connection and logging settings
- **Actuator health checks** – built-in liveness/readiness plus two custom `HealthIndicator`s: database connectivity and training-type reference-data seeding
- **Prometheus metrics** – three custom Micrometer counters (authentication success, authentication failure, trainings created) exposed at `/actuator/prometheus`, alongside Boot's built-in JVM/HTTP metrics
- **Unit tested** – JUnit 5 + Mockito for services/mappers/health indicators/metrics/security, MockMvc for controllers, Spring Data JPA repository tests against H2, with JaCoCo coverage

---

## API Endpoints

| # | Method | Path | Purpose | Auth required |
|---|--------|------|---------|---|
| 1 | POST | `/api/trainees` | Register a trainee | No |
| 2 | POST | `/api/trainers` | Register a trainer | No |
| 3 | POST | `/api/login` | Login (issues a JWT) | No |
| 4 | PUT | `/api/login` | Change password | Yes |
| 5 | POST | `/api/logout` | Logout (blacklists the current token) | Yes |
| 6 | GET | `/api/trainees/{username}` | Get trainee profile | Yes |
| 7 | PUT | `/api/trainees/{username}` | Update trainee profile | Yes |
| 8 | DELETE | `/api/trainees/{username}` | Delete trainee profile (cascades trainings) | Yes |
| 9 | PATCH | `/api/trainees/{username}/status` | Activate/deactivate a trainee | Yes |
| 10 | GET | `/api/trainees/{username}/unassigned-trainers` | Active trainers not yet assigned to the trainee | Yes |
| 11 | PUT | `/api/trainees/{username}/trainers` | Replace a trainee's trainer list | Yes |
| 12 | GET | `/api/trainers/{username}` | Get trainer profile | Yes |
| 13 | PUT | `/api/trainers/{username}` | Update trainer profile | Yes |
| 14 | PATCH | `/api/trainers/{username}/status` | Activate/deactivate a trainer | Yes |
| 15 | POST | `/api/trainings` | Add a training | Yes |
| 16 | GET | `/api/trainings/trainee/{username}` | Trainee's trainings (filterable by period, trainer name, training type) | Yes |
| 17 | GET | `/api/trainings/trainer/{username}` | Trainer's trainings (filterable by period, trainee name) | Yes |
| 18 | GET | `/api/trainings/types` | List training types | Yes |

All paths above are relative to the configured context path — `/gym-crm` by default (see [Configuration](#configuration)). Protected endpoints require `Authorization: Bearer <token>`.

---

## Observability

### Health

`GET /gym-crm/actuator/health` aggregates Boot's own checks with two custom indicators:

| Indicator | Reports DOWN when |
|---|---|
| `database` | The `DataSource` cannot hand out a valid connection |
| `trainingTypeReferenceData` | The `training_type` table has no rows (seeder hasn't run, or schema was reset without reseeding) |

`management.endpoint.health.show-details` is `always` on `local`, `when-authorized` by default, and `never` on `prod`.

### Metrics

`GET /gym-crm/actuator/prometheus` exposes, alongside Boot's default JVM/HTTP metrics:

| Metric | Type | Tags | Recorded when |
|---|---|---|---|
| `gym_crm_authentication_attempts_total` | Counter | `result=success` | Every successful `AuthenticationService.authenticate(...)` call |
| `gym_crm_authentication_attempts_total` | Counter | `result=failure` | Every failed `AuthenticationService.authenticate(...)` call |
| `gym_crm_trainings_created_total` | Counter | — | Every successful `TrainingService.addTraining(...)` |

All three are registered in `GymCrmMetrics`, the single place new metrics should be added.

---

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.3.4 |
| Spring Framework (Web/Data/Security/TX) | 6.1.x (via Boot BOM) |
| Spring Data JPA / Hibernate Core | 6.5.x (via Boot BOM) |
| Spring Security | via Boot BOM |
| JJWT (JWT issuing/parsing) | 0.12.6 |
| Micrometer / Prometheus registry | via Boot BOM |
| PostgreSQL (runtime) / H2 (tests) | via Boot BOM |
| HikariCP | via Boot BOM |
| Jackson (incl. JSR-310) | via Boot BOM |
| Jakarta Bean Validation / Hibernate Validator | via `spring-boot-starter-validation` |
| springdoc-openapi (OpenAPI 3 / Swagger UI) | 2.6.0 |
| Lombok | 1.18.38 |
| JUnit 5 / Mockito / Spring Test | via `spring-boot-starter-test` |
| Logback | via Boot BOM |
| JaCoCo | 0.8.12 |
| Maven | 3.6+ |
| Packaging | Executable jar (embedded Tomcat) |

---

## Project Structure

```
gym-crm/
├── src/main/java/com/gym/crm/
│   ├── Application.java                # @SpringBootApplication entry point
│   ├── config/
│   │   ├── WebConfig.java              # Registers the REST logging interceptor
│   │   ├── SecurityConfig.java         # JWT filter chain, CORS, password encoder, public endpoints
│   │   └── OpenApiConfig.java          # OpenAPI info + bearer-auth security scheme for Swagger UI
│   ├── health/                         # DatabaseHealthIndicator, TrainingTypeReferenceDataHealthIndicator
│   ├── metrics/GymCrmMetrics.java      # Custom Prometheus counters
│   ├── controller/                     # TraineeController, TrainerController, TrainingController, AuthenticationController
│   ├── dto/                            # Request/response DTOs, grouped by trainee/trainer/training/auth/common
│   ├── mapper/                         # Entity <-> DTO mapping (TraineeMapper, TrainerMapper, TrainingMapper, ReferenceMapper)
│   ├── domain/                         # User, Trainee, Trainer, Training, TrainingType (JPA entities)
│   ├── repository/                     # Spring Data JpaRepositorys: UserRepository, TraineeRepository, TrainerRepository, TrainingRepository, TrainingTypeRepository
│   ├── service/                        # TraineeService, TrainerService, TrainingService, UserProfileService, AuthenticationService
│   ├── security/                       # JwtService, JwtAuthenticationFilter, GymUserDetailsService, LoginAttemptService,
│   │                                    # TokenBlacklistService, RestAuthenticationEntryPoint, RestAccessDeniedHandler
│   ├── logging/                        # TransactionIdFilter, RestLoggingInterceptor, LoggingConstants
│   ├── init/TrainingTypeSeeder.java    # Seeds default training types on startup
│   ├── exception/                      # ValidationException, EntityNotFoundException, AuthenticationException, AccountLockedException, GlobalExceptionHandler
│   └── util/ValidationUtils.java
├── src/main/resources/
│   ├── application.properties          # Default active profile, context path, JWT/CORS settings, actuator exposure
│   ├── application-local.properties    # Local dev DB (localhost:5433), ddl-auto=update, verbose logging
│   ├── application-dev.properties      # Shared/dev-container DB, credentials from env, ddl-auto=update
│   ├── application-stg.properties      # Staging DB, ddl-auto=validate (no automatic schema changes), credentials required from env
│   ├── application-prod.properties     # Production DB, ddl-auto=validate, credentials required from env, health details hidden
│   └── logback.xml                     # Logging configuration
├── docker-compose.yml                  # Local PostgreSQL + app container (dev profile)
├── Dockerfile                          # Multi-stage build: Maven build -> plain JRE runtime running the jar
└── src/test/java/com/gym/crm/          # Unit tests, MockMvc controller tests, Spring Data JPA repository tests against H2
```

---

## Architecture

- **Web layer:** Spring Boot auto-configures the embedded Tomcat, `DispatcherServlet`, Jackson `ObjectMapper` (JSR-310 module registered), and Bean Validation. `WebConfig` registers `RestLoggingInterceptor`; `TransactionIdFilter` is picked up as a `Filter` bean.
- **Controllers → Services:** `@RestController`s call `TraineeService` / `TrainerService` / `TrainingService` / `AuthenticationService` directly (constructor injection) and translate entities to/from DTOs via static mapper classes.
- **Persistence:** Entities are mapped with JPA annotations (`@Entity`, `@OneToOne`, `@ManyToOne`, `@ManyToMany`, `@OneToMany`) and managed through Spring Data JPA `JpaRepository` interfaces, with derived/`@Query` methods for username lookups, filtered training search, and unassigned-trainer lookups. `Trainee`↔`User` and `Trainer`↔`User` are `@OneToOne`; `Trainee`↔`Trainer` is `@ManyToMany`; `Training` is `@ManyToOne` to `Trainee`, `Trainer`, and `TrainingType`.
- **Transactions:** Service methods run inside `@Transactional` boundaries (read-only where appropriate); commits are handled by Spring's JPA transaction manager.
- **Validation & errors:** Request DTOs carry Jakarta Bean Validation annotations; `GlobalExceptionHandler` (`@RestControllerAdvice`) converts `ValidationException`, `EntityNotFoundException`, `AuthenticationException`, `AccountLockedException`, and constraint/argument-binding failures into structured JSON error bodies with appropriate HTTP status codes (400/401/404/423/405/500).
- **Authentication:** `POST /api/login` authenticates via Spring Security's `AuthenticationManager` (backed by `GymUserDetailsService` + BCrypt) and returns a signed JWT (`JwtService`). `JwtAuthenticationFilter` validates the bearer token on every request and populates the `SecurityContext`; `SecurityConfig` permits `POST /api/trainees`, `POST /api/trainers`, `/api/login`, `/actuator/**`, and the Swagger UI/OpenAPI endpoints, and requires authentication for everything else. `RestAuthenticationEntryPoint`/`RestAccessDeniedHandler` turn unauthenticated/forbidden requests into JSON `401`/`403` responses. `LoginAttemptService` locks an account for 5 minutes after 3 consecutive failures (`423`). `POST /api/logout` blacklists the token's JTI in `TokenBlacklistService` until its natural expiry. Every login attempt increments `gym_crm_authentication_attempts_total{result=success|failure}`.
- **Logging:** `TransactionIdFilter` runs first, generating/propagating a transaction id via MDC and a response header; `RestLoggingInterceptor` runs next and logs each call's method, URI, status, and duration.
- **Health & metrics:** `DatabaseHealthIndicator` and `TrainingTypeReferenceDataHealthIndicator` are picked up automatically by Boot's actuator as `HealthIndicator` beans. `GymCrmMetrics` wraps a `MeterRegistry` and is injected into `AuthenticationService`/`TrainingService` to record the custom counters.

---

## Getting Started

### Prerequisites
- JDK 17+
- Maven 3.6+ (or use the Docker instructions below, no local Maven/JDK needed)
- Docker (for PostgreSQL, and optionally the whole app), or your own PostgreSQL server

### Option 1 — Docker Compose (app + database, `dev` profile)

```bash
docker compose up --build
```

Starts PostgreSQL and the app together; the app connects to Postgres over the Docker network using the `dev` profile. API available at `http://localhost:8080/gym-crm/api/...`.

### Option 2 — Run locally with Maven (`local` profile)

Start just the database:
```bash
docker compose up -d postgres
```

Then either build and run the jar:
```bash
mvn clean package
java -jar target/gym-crm.jar
```
or run directly without packaging:
```bash
mvn spring-boot:run
```

`local` is the default active profile and already points at `localhost:5433` (the port `docker-compose.yml` maps Postgres to).

### Running a different profile

```bash
java -jar target/gym-crm.jar --spring.profiles.active=stg
# or
SPRING_PROFILES_ACTIVE=prod java -jar target/gym-crm.jar
```
`stg` and `prod` require `DB_USERNAME` and `DB_PASSWORD` to be set in the environment — they have no defaults, on purpose, so credentials never end up hardcoded or logged. All profiles also honor `DB_HOST`/`DB_PORT`/`DB_NAME` overrides.

---

## Configuration

Common settings (default active profile, context path, JWT/CORS, actuator exposure) live in `src/main/resources/application.properties`. Per-environment database and logging settings live in `application-{profile}.properties`:

```properties
# application-local.properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5433}/${DB_NAME:gymcrm}
spring.datasource.username=${DB_USERNAME:gymcrm}
spring.datasource.password=${DB_PASSWORD:gymcrm}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

`dev` follows the same shape with host/credentials read from environment variables (defaulting to the docker-compose values). `stg` and `prod` set `spring.jpa.hibernate.ddl-auto=validate` (no automatic schema changes against real environments) and require `DB_USERNAME`/`DB_PASSWORD` from the environment with no fallback.

Other common settings in `application.properties`:

| Property | Purpose |
|---|---|
| `server.servlet.context-path=/gym-crm` | Keeps the API's base path |
| `jwt.secret` (env `JWT_SECRET`) | Signing key for issued JWTs — **override in every real environment** |
| `jwt.expiration-ms` (env `JWT_EXPIRATION_MS`) | Token lifetime in milliseconds (default 1 hour) |
| `cors.allowed-origins` (env `CORS_ALLOWED_ORIGINS`) | Allowed CORS origins (default `*`) |
| `management.endpoints.web.exposure.include` | Exposes `health`, `info`, `prometheus`, `metrics` |

---

## Usage Example

```bash
# Register a trainee
curl -X POST http://localhost:8080/gym-crm/api/trainees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Smith","dateOfBirth":"1990-05-20","address":"Baker Street 221B"}'
# -> {"username":"John.Smith","password":"<random 10 chars>"}

# Login (returns a JWT)
curl -X POST http://localhost:8080/gym-crm/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"John.Smith","password":"<password>"}'
# -> {"token":"<jwt>"}

# Get profile (every endpoint except registration/login requires a bearer token)
curl -H "Authorization: Bearer <jwt>" http://localhost:8080/gym-crm/api/trainees/John.Smith

# Logout (blacklists the token)
curl -X POST -H "Authorization: Bearer <jwt>" http://localhost:8080/gym-crm/api/logout

# Health check
curl http://localhost:8080/gym-crm/actuator/health

# Prometheus metrics
curl http://localhost:8080/gym-crm/actuator/prometheus

# Swagger UI
open http://localhost:8080/gym-crm/swagger-ui.html
```

---

## Testing

Run all tests locally:
```bash
mvn clean test
```

Or without installing Maven/JDK, using Docker:
```bash
docker run --rm -v "$(pwd):/app" -w /app maven:3.9-eclipse-temurin-17 mvn clean test
```

- Controller tests use MockMvc to exercise each endpoint's request/response contract and status codes
- Service tests mock repository and `GymCrmMetrics` dependencies with Mockito, covering validation, username-collision handling, and metric recording
- Repository tests run against a real H2-backed `JpaRepository` context
- Health indicator tests mock the `DataSource`/repository dependency and assert on `Health` status and details for both the up and down branches
- Metrics tests use a real `SimpleMeterRegistry` and assert on recorded counter values
- Security tests cover `JwtService`, `JwtAuthenticationFilter`, `GymUserDetailsService`, `LoginAttemptService`, `TokenBlacklistService`, and the entry-point/access-denied handlers
- Mapper, logging (`TransactionIdFilter`, `RestLoggingInterceptor`), and exception-handler behavior are covered separately
- Tests follow the **FIRST** principles (Fast, Isolated, Repeatable, Self-validating, Timely)

Coverage report (JaCoCo, wired into the `test` phase):
```
target/site/jacoco/index.html
```

---

## Requirements Coverage

| Area | Status |
|---|---|
| All 18 endpoints, correct HTTP methods & request/response shapes | ✅ |
| Username/password generation | ✅ |
| Can't register as both trainer and trainee | ✅ (enforced implicitly via global username uniqueness on `users`) |
| Users 1:1 with Trainer/Trainee; Trainee↔Trainer many-to-many | ✅ |
| Training create-only via REST (no update/delete) | ✅ |
| Username immutable | ✅ |
| Field types (duration numeric, dates as `LocalDate`, `isActive` boolean) | ✅ |
| Training types constant, seeded, read-only via API | ✅ |
| Per-field request validation | ✅ |
| Global error handling | ✅ |
| Hard delete of trainee cascades to trainings | ✅ |
| Activate/deactivate is non-idempotent (PATCH, not PUT) | ✅ |
| Two-level logging (transaction id + per-call) | ✅ |
| OpenAPI/Swagger UI documentation | ✅ |
| JWT-based authentication required on every endpoint except registration/login | ✅ |
| Login lockout after repeated failures | ✅ |
| Logout / token revocation | ✅ |
| Spring Boot application with Spring Data JPA persistence | ✅ |
| Actuator enabled | ✅ |
| At least two custom health indicators | ✅ (`database`, `trainingTypeReferenceData`) |
| At least two custom Prometheus metrics | ✅ (`gym_crm_authentication_attempts_total`, `gym_crm_trainings_created_total`) |
| Environment profiles (`local`/`dev`/`stg`/`prod`) with per-environment DB properties | ✅ |
| Unit test coverage (incl. security, health indicators & metrics) | ✅ |

### Notes

- **Authentication** is JWT-based, not HTTP Basic: `SecurityConfig` wires a stateless `SecurityFilterChain` with `JwtAuthenticationFilter` ahead of Spring Security's standard username/password filter, permitting only registration, login, actuator, and Swagger UI/OpenAPI paths; everything else requires a valid, non-blacklisted bearer token.
- **Persistence uses Spring Data JPA**, not a hand-rolled Hibernate DAO layer — each aggregate has a `JpaRepository` interface, and cross-cutting queries (filtered training search, unassigned trainers, username lookups) are expressed as derived query methods or `@Query`.
- **API documentation** is generated at runtime by springdoc-openapi directly from the Spring MVC controller signatures and OpenAPI 3 annotations, and served at `/swagger-ui.html` and `/v3/api-docs`.

---

## License

This project is provided for educational purposes.
