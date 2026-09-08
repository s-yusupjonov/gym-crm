# Gym CRM System

A Spring Boot REST API for a Gym CRM (Customer Relationship Management) system. It exposes **Trainer**, **Trainee**, and **Training** management as a `RestController`-based JSON API, packaged as an executable jar with an embedded servlet container, and persisted with **Hibernate 6** on top of **PostgreSQL**.

---

## Features

- **17 REST endpoints** covering trainee/trainer registration, login, profile management, trainer-assignment, training scheduling, and training-type lookup (full list below)
- **Automatic username generation** – `firstName.lastName`, with a numeric suffix on duplicates (e.g. `John.Smith`, `John.Smith1`), unique across trainees and trainers alike (backed by the shared `users` table), which also makes it impossible to register the same identity as both a trainee and a trainer
- **Automatic password generation** – random 10-character string
- **Authentication enforced on every protected endpoint** – an `AuthenticationInterceptor` requires an HTTP Basic `Authorization` header (checked via `AuthenticationService`) on every request except `POST /api/trainees`, `POST /api/trainers`, and `/api/login` (which authenticates as part of its own logic)
- **Persistence with Hibernate** – entities mapped with JPA annotations, managed via a `SessionFactory`-backed generic DAO layer (plain Hibernate, not Spring Data JPA); hard delete of a trainee cascades to their trainings
- **Training type seeding** – default training types are seeded on startup if the table is empty, and are read-only from the API
- **Request validation** – Jakarta Bean Validation on every request DTO, with a `@RestControllerAdvice` translating validation/auth/not-found errors into structured JSON error responses
- **Two-level logging** – a servlet `Filter` (`TransactionIdFilter`) assigns/propagates a transaction id (via MDC + response header) across each request, and a `HandlerInterceptor` (`RestLoggingInterceptor`) logs each REST call's method, URI, status, and duration
- **API documentation** – every controller method annotated with Swagger 2 (`@Api`, `@ApiOperation`, `@ApiParam`, `@ApiResponses`)
- **Environment profiles** – `local`, `dev`, `stg`, `prod` Spring profiles, each with its own database connection and logging settings
- **Actuator health checks** – built-in liveness/readiness plus two custom `HealthIndicator`s: database connectivity and training-type reference-data seeding
- **Prometheus metrics** – two custom Micrometer counters (authentication attempts, trainings created) exposed at `/actuator/prometheus`, alongside Boot's built-in JVM/HTTP metrics
- **Unit tested** – JUnit 5 + Mockito for services/mappers/health indicators/metrics, MockMvc for controllers, real Hibernate sessions for DAOs, with JaCoCo coverage

---

## API Endpoints

| # | Method | Path | Purpose |
|---|--------|------|---------|
| 1 | POST | `/api/trainees` | Register a trainee |
| 2 | POST | `/api/trainers` | Register a trainer |
| 3 | GET | `/api/login` | Login (credential check) |
| 4 | PUT | `/api/login` | Change password |
| 5 | GET | `/api/trainees/{username}` | Get trainee profile |
| 6 | PUT | `/api/trainees/{username}` | Update trainee profile |
| 7 | DELETE | `/api/trainees/{username}` | Delete trainee profile (cascades trainings) |
| 8 | GET | `/api/trainers/{username}` | Get trainer profile |
| 9 | PUT | `/api/trainers/{username}` | Update trainer profile |
| 10 | GET | `/api/trainees/{username}/unassigned-trainers` | Active trainers not yet assigned to the trainee |
| 11 | PUT | `/api/trainees/{username}/trainers` | Replace a trainee's trainer list |
| 12 | GET | `/api/trainings/trainee/{username}` | Trainee's trainings (filterable by period, trainer name, training type) |
| 13 | GET | `/api/trainings/trainer/{username}` | Trainer's trainings (filterable by period, trainee name) |
| 14 | POST | `/api/trainings` | Add a training |
| 15 | PATCH | `/api/trainees/{username}/status` | Activate/deactivate a trainee |
| 16 | PATCH | `/api/trainers/{username}/status` | Activate/deactivate a trainer |
| 17 | GET | `/api/trainings/types` | List training types |

All paths above are relative to the configured context path — `/gym-crm` by default (see [Configuration](#configuration)).

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
| `gym_crm_authentication_attempts_total` | Counter | `result=success\|failure` | Every call to `AuthenticationService.authenticate(...)` |
| `gym_crm_trainings_created_total` | Counter | — | Every successful `TrainingService.addTraining(...)` |

Both are registered in `GymCrmMetrics`, the single place new metrics should be added.

---

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.3.4 |
| Spring Framework (Web/ORM/TX) | 6.1.x (via Boot BOM) |
| Hibernate Core | 6.5.x (via Boot BOM) |
| Micrometer / Prometheus registry | via Boot BOM |
| PostgreSQL (runtime) / H2 (tests) | 42.7.3 / (via Boot BOM) |
| HikariCP | via Boot BOM |
| Jackson (incl. JSR-310) | via Boot BOM |
| Jakarta Bean Validation / Hibernate Validator | via `spring-boot-starter-validation` |
| Swagger annotations | 1.6.14 |
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
│   ├── Application.java                # @SpringBootApplication (excludes HibernateJpaAutoConfiguration)
│   ├── config/
│   │   ├── WebConfig.java              # Registers logging + auth interceptors (Boot autoconfigures the rest of MVC)
│   │   ├── InterceptorConfig.java      # Interceptor bean definitions
│   │   └── HibernateConfig.java        # DataSource, SessionFactory, TransactionManager (profile-aware properties)
│   ├── health/                         # DatabaseHealthIndicator, TrainingTypeReferenceDataHealthIndicator
│   ├── metrics/GymCrmMetrics.java      # Custom Prometheus counters
│   ├── controller/                     # TraineeController, TrainerController, TrainingController, AuthenticationController
│   ├── dto/                            # Request/response DTOs, grouped by trainee/trainer/training/auth/common
│   ├── mapper/                         # Entity <-> DTO mapping (TraineeMapper, TrainerMapper, TrainingMapper, ReferenceMapper)
│   ├── domain/                         # User, Trainee, Trainer, Training, TrainingType (JPA entities)
│   ├── dao/                            # AbstractDao<T> + UserDao, TraineeDao, TrainerDao, TrainingDao, TrainingTypeDao
│   ├── service/                        # TraineeService, TrainerService, TrainingService, UserProfileService, AuthenticationService
│   ├── security/AuthenticationInterceptor.java  # Enforces HTTP Basic credential checks on every endpoint except registration/login
│   ├── logging/                        # TransactionIdFilter, RestLoggingInterceptor, LoggingConstants
│   ├── init/TrainingTypeSeeder.java    # Seeds default training types on startup
│   ├── exception/                      # ValidationException, EntityNotFoundException, AuthenticationException, GlobalExceptionHandler
│   └── util/ValidationUtils.java
├── src/main/resources/
│   ├── application.properties          # Default active profile, actuator exposure
│   ├── application-local.properties    # Local dev DB (localhost:5433)
│   ├── application-dev.properties      # Shared dev DB, credentials from env
│   ├── application-stg.properties      # Staging DB, schema validated (not auto-updated), credentials required from env
│   ├── application-prod.properties     # Production DB, schema validated, credentials required from env, health details hidden
│   └── logback.xml                     # Logging configuration
├── docker-compose.yml                  # Local PostgreSQL + app container (dev profile)
├── Dockerfile                          # Multi-stage build: Maven build -> plain JRE runtime running the jar
└── src/test/java/com/gym/crm/          # Unit + MockMvc controller tests, DAO tests against a real H2 SessionFactory
```

---

## Architecture

- **Web layer:** Spring Boot auto-configures the embedded Tomcat, `DispatcherServlet`, Jackson `ObjectMapper` (JSR-310 module registered, timestamps disabled by default), and Bean Validation. `WebConfig` only adds what Boot doesn't cover: the logging and authentication interceptors, plus `CharacterEncodingFilter`/`TransactionIdFilter` remain as before.
- **Controllers → Services:** `@RestController`s call `TraineeService` / `TrainerService` / `TrainingService` / `AuthenticationService` directly (constructor injection) and translate entities to/from DTOs via static mapper classes.
- **Persistence:** Entities are mapped with JPA annotations (`@Entity`, `@OneToOne`, `@ManyToMany`, `@OneToMany`) and managed through `AbstractDao<T>`, a generic base DAO built on `SessionFactory#getCurrentSession()` — plain Hibernate, not Spring Data JPA. Entity-specific DAOs add HQL/Criteria queries (username lookups, filtered training search, unassigned-trainer lookup). `Application` explicitly excludes `HibernateJpaAutoConfiguration`: Hibernate 6's `SessionFactory` implements `EntityManagerFactory`, and leaving that autoconfiguration enabled makes Boot wrap it with `OpenEntityManagerInViewInterceptor`, which collides with `HibernateTransactionManager`'s own resource binding.
- **Transactions:** Service methods run inside `@Transactional` boundaries (read-only where appropriate); commits are handled by Spring's `HibernateTransactionManager`.
- **Validation & errors:** Request DTOs carry Jakarta Bean Validation annotations; `GlobalExceptionHandler` (`@RestControllerAdvice`) converts `ValidationException`, `EntityNotFoundException`, `AuthenticationException`, and constraint violations into structured JSON error bodies with appropriate HTTP status codes.
- **Authentication:** `AuthenticationInterceptor` (a `HandlerInterceptor`, registered in `WebConfig` for `/api/**`) requires and verifies an HTTP Basic `Authorization` header via `AuthenticationService` on every request, except `POST /api/trainees`, `POST /api/trainers` (registration) and `/api/login` (which authenticates as part of its own logic). A failed check throws `AuthenticationException`, which `GlobalExceptionHandler` turns into a `401`. Every call also increments the `gym_crm_authentication_attempts_total` counter.
- **Logging:** `TransactionIdFilter` runs first, generating/propagating a transaction id via MDC and a response header; `RestLoggingInterceptor` runs next and logs each call's method, URI, status, and duration (including failed-auth attempts, since it's registered before `AuthenticationInterceptor`).
- **Health & metrics:** `DatabaseHealthIndicator` and `TrainingTypeReferenceDataHealthIndicator` are picked up automatically by Boot's actuator as `HealthIndicator` beans. `GymCrmMetrics` wraps a `MeterRegistry` and is injected into `AuthenticationService`/`TrainingService` to record the two custom counters.

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
`stg` and `prod` require `DB_HOST`, `DB_USERNAME`, and `DB_PASSWORD` to be set in the environment — they have no defaults, on purpose, so credentials never end up hardcoded or logged.

---

## Configuration

Common settings (default active profile, actuator exposure) live in `src/main/resources/application.properties`. Per-environment database and logging settings live in `application-{profile}.properties`:

```properties
# application-local.properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://localhost:5433/gymcrm
db.username=gymcrm
db.password=gymcrm

hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
hibernate.format_sql=true
```

`dev` follows the same shape with host/credentials read from environment variables (defaulting to the docker-compose values). `stg` and `prod` set `hibernate.hbm2ddl.auto=validate` (no automatic schema changes against real environments) and require `DB_USERNAME`/`DB_PASSWORD` from the environment with no fallback.

`server.servlet.context-path=/gym-crm` is set in the common `application.properties` so the API keeps the same base path it had under the previous WAR-on-Tomcat deployment.

---

## Usage Example

```bash
# Register a trainee
curl -X POST http://localhost:8080/gym-crm/api/trainees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Smith","dateOfBirth":"1990-05-20","address":"Baker Street 221B"}'
# -> {"username":"John.Smith","password":"<random 10 chars>"}

# Login
curl "http://localhost:8080/gym-crm/api/login?username=John.Smith&password=<password>"

# Get profile (every endpoint except registration/login requires HTTP Basic credentials)
curl -u John.Smith:<password> http://localhost:8080/gym-crm/api/trainees/John.Smith

# Health check
curl http://localhost:8080/gym-crm/actuator/health

# Prometheus metrics
curl http://localhost:8080/gym-crm/actuator/prometheus
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
- Service tests mock DAO and `GymCrmMetrics` dependencies with Mockito, covering validation, username-collision handling, and metric recording
- DAO tests run against a real H2 `SessionFactory` with transaction rollback after each test
- Health indicator tests mock the `DataSource`/DAO dependency and assert on `Health` status and details for both the up and down branches
- Metrics tests use a real `SimpleMeterRegistry` and assert on recorded counter values
- Mapper, logging (`TransactionIdFilter`, `RestLoggingInterceptor`), security (`AuthenticationInterceptor`), and exception-handler behavior are covered separately
- Tests follow the **FIRST** principles (Fast, Isolated, Repeatable, Self-validating, Timely)

Coverage report (JaCoCo, wired into the `test` phase):
```
target/site/jacoco/index.html
```

---

## Requirements Coverage

Checked against the REST task spec (`Task_Rest.pdf`) and the Spring Boot follow-up task:

| Area | Status |
|---|---|
| All 17 endpoints, correct HTTP methods & request/response shapes | ✅ |
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
| Swagger 2 annotations on controller methods | ✅ |
| Auth required on every endpoint except registration | ✅ |
| Converted to a Spring Boot application | ✅ |
| Actuator enabled | ✅ |
| At least two custom health indicators | ✅ (`database`, `trainingTypeReferenceData`) |
| At least two custom Prometheus metrics | ✅ (`gym_crm_authentication_attempts_total`, `gym_crm_trainings_created_total`) |
| Environment profiles (`local`/`dev`/`stg`/`prod`) with per-environment DB properties | ✅ |
| Unit test coverage (incl. new health indicators & metrics) | ✅ |

### Notes

- **Authentication enforcement** is implemented as `AuthenticationInterceptor`, a `HandlerInterceptor` registered for `/api/**` in `WebConfig`, requiring and verifying an HTTP Basic `Authorization` header via `AuthenticationService.authenticate(...)` on every call except `POST /api/trainees`, `POST /api/trainers` (registration), and `/api/login` (which is itself the credential check). It's unit-tested in isolation (`AuthenticationInterceptorTest`). Controller tests (`*ControllerTest`) build `MockMvc` with `standaloneSetup(controller)`, which doesn't wire `WebConfig`'s interceptors, so they exercise controller logic without needing Authorization headers — this is deliberate test isolation, not a gap in enforcement, since the interceptor itself is fully covered separately and is registered ahead of every controller in the real dispatch chain.
- **`HibernateJpaAutoConfiguration` is explicitly excluded** in `Application`. This app uses a plain Hibernate `Session`/`SessionFactory` (see `AbstractDao`), not JPA's `EntityManager`. Left enabled, Boot detects the `SessionFactory` bean as a JPA `EntityManagerFactory` (Hibernate 6's `SessionFactory` implements that interface) and wires `OpenEntityManagerInViewInterceptor` around it, which binds an `EntityManagerHolder` under the same key `HibernateTransactionManager` uses for its `SessionHolder` — causing a `ClassCastException` on every transactional request.
- The `GymFacade` and pre-REST-module `Application.main` classes have been removed, since the REST controllers never used them and they were dead code.

---

## License

This project is provided for educational purposes.
