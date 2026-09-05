# Gym CRM System

A Spring MVC REST API for a Gym CRM (Customer Relationship Management) system. It exposes **Trainer**, **Trainee**, and
**Training** management as a `RestController`-based JSON API, deployed as a WAR, and persisted with **Hibernate 6** on
top of **PostgreSQL**.

---

## Features

- **17 REST endpoints** covering trainee/trainer registration, login, profile management, trainer-assignment, training
  scheduling, and training-type lookup (full list below)
- **Automatic username generation** – `firstName.lastName`, with a numeric suffix on duplicates (e.g. `John.Smith`,
  `John.Smith1`), unique across trainees and trainers alike (backed by the shared `users` table), which also makes it
  impossible to register the same identity as both a trainee and a trainer
- **Automatic password generation** – random 10-character string
- **Authentication enforced on every protected endpoint** – an `AuthenticationInterceptor` requires an HTTP Basic
  `Authorization` header (checked via `AuthenticationService`) on every request except `POST /api/trainees`,
  `POST /api/trainers`, and `/api/login` (which authenticates as part of its own logic)
- **Persistence with Hibernate** – entities mapped with JPA annotations, managed via a `SessionFactory`-backed generic
  DAO layer; hard delete of a trainee cascades to their trainings
- **Training type seeding** – default training types are seeded on startup if the table is empty, and are read-only from
  the API
- **Request validation** – Jakarta Bean Validation on every request DTO, with a `@RestControllerAdvice` translating
  validation/auth/not-found errors into structured JSON error responses
- **Two-level logging** – a servlet `Filter` (`TransactionIdFilter`) assigns/propagates a transaction id (via MDC +
  response header) across each request, and a `HandlerInterceptor` (`RestLoggingInterceptor`) logs each REST call's
  method, URI, status, and duration
- **API documentation** – every controller method annotated with Swagger 2 (`@Api`, `@ApiOperation`, `@ApiParam`,
  `@ApiResponses`)
- **Unit tested** – JUnit 5 + Mockito for services/mappers, MockMvc for controllers, real Hibernate sessions for DAOs,
  with JaCoCo coverage

---

## API Endpoints

| #  | Method | Path                                           | Purpose                                                                 |
|----|--------|------------------------------------------------|-------------------------------------------------------------------------|
| 1  | POST   | `/api/trainees`                                | Register a trainee                                                      |
| 2  | POST   | `/api/trainers`                                | Register a trainer                                                      |
| 3  | GET    | `/api/login`                                   | Login (credential check)                                                |
| 4  | PUT    | `/api/login`                                   | Change password                                                         |
| 5  | GET    | `/api/trainees/{username}`                     | Get trainee profile                                                     |
| 6  | PUT    | `/api/trainees/{username}`                     | Update trainee profile                                                  |
| 7  | DELETE | `/api/trainees/{username}`                     | Delete trainee profile (cascades trainings)                             |
| 8  | GET    | `/api/trainers/{username}`                     | Get trainer profile                                                     |
| 9  | PUT    | `/api/trainers/{username}`                     | Update trainer profile                                                  |
| 10 | GET    | `/api/trainees/{username}/unassigned-trainers` | Active trainers not yet assigned to the trainee                         |
| 11 | PUT    | `/api/trainees/{username}/trainers`            | Replace a trainee's trainer list                                        |
| 12 | GET    | `/api/trainings/trainee/{username}`            | Trainee's trainings (filterable by period, trainer name, training type) |
| 13 | GET    | `/api/trainings/trainer/{username}`            | Trainer's trainings (filterable by period, trainee name)                |
| 14 | POST   | `/api/trainings`                               | Add a training                                                          |
| 15 | PATCH  | `/api/trainees/{username}/status`              | Activate/deactivate a trainee                                           |
| 16 | PATCH  | `/api/trainers/{username}/status`              | Activate/deactivate a trainer                                           |
| 17 | GET    | `/api/trainings/types`                         | List training types                                                     |

---

## Tech Stack

| Component                                     | Version                                                |
|-----------------------------------------------|--------------------------------------------------------|
| Java                                          | 17                                                     |
| Spring Context / ORM / TX / WebMVC            | 6.1.6                                                  |
| Hibernate Core                                | 6.5.2.Final                                            |
| PostgreSQL (runtime) / H2 (tests)             | 42.7.3 / 2.2.224                                       |
| HikariCP                                      | 5.1.0                                                  |
| Jackson (incl. JSR-310)                       | 2.17.1                                                 |
| Jakarta Bean Validation / Hibernate Validator | 3.0.2 / 8.0.1.Final                                    |
| Swagger annotations                           | 1.6.14                                                 |
| Lombok                                        | 1.18.38                                                |
| JUnit 5                                       | 5.10.2                                                 |
| Mockito                                       | 5.11.0                                                 |
| Spring Test (MockMvc)                         | 6.1.6                                                  |
| Logback                                       | 1.5.6                                                  |
| JaCoCo                                        | 0.8.12                                                 |
| Maven                                         | 3.6+                                                   |
| Packaging                                     | WAR (deploy to a Servlet 6 container, e.g. Tomcat 10+) |

---

## Project Structure

```
gym-crm/
├── src/main/java/com/gym/crm/
│   ├── config/
│   │   ├── WebAppInitializer.java      # Registers DispatcherServlet, root context, encoding + transaction-id filters
│   │   ├── RootConfig.java             # Component-scans dao/service/mapper/util, imports HibernateConfig
│   │   ├── WebConfig.java              # @EnableWebMvc, Jackson config, Bean Validation, logging + auth interceptors
│   │   └── HibernateConfig.java        # DataSource, SessionFactory, TransactionManager
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
│   ├── application.properties          # DB connection + Hibernate properties
│   └── logback.xml                     # Logging configuration
├── docker-compose.yml                  # Local PostgreSQL for dev/test
└── src/test/java/com/gym/crm/          # Unit + MockMvc controller tests, DAO tests against a real H2 SessionFactory
```

---

## Architecture

- **Web layer:** `WebAppInitializer` (programmatic `web.xml` replacement) registers a root `ApplicationContext` (
  `RootConfig`, service/DAO layer) and a child web `ApplicationContext` (`WebConfig`, controllers) behind Spring's
  `DispatcherServlet`, plus a `CharacterEncodingFilter` and `TransactionIdFilter`.
- **Controllers → Services:** `@RestController`s call `TraineeService` / `TrainerService` / `TrainingService` /
  `AuthenticationService` directly (constructor injection) and translate entities to/from DTOs via static mapper
  classes.
- **Persistence:** Entities are mapped with JPA annotations (`@Entity`, `@OneToOne`, `@ManyToMany`, `@OneToMany`) and
  managed through `AbstractDao<T>`, a generic base DAO built on `SessionFactory#getCurrentSession()`. Entity-specific
  DAOs add HQL/Criteria queries (username lookups, filtered training search, unassigned-trainer lookup).
- **Transactions:** Service methods run inside `@Transactional` boundaries (read-only where appropriate); commits are
  handled by Spring's `HibernateTransactionManager`.
- **Validation & errors:** Request DTOs carry Jakarta Bean Validation annotations; `GlobalExceptionHandler` (
  `@RestControllerAdvice`) converts `ValidationException`, `EntityNotFoundException`, `AuthenticationException`, and
  constraint violations into structured JSON error bodies with appropriate HTTP status codes.
- **Authentication:** `AuthenticationInterceptor` (a `HandlerInterceptor`, registered in `WebConfig` for `/api/**`)
  requires and verifies an HTTP Basic `Authorization` header via `AuthenticationService` on every request, except
  `POST /api/trainees`, `POST /api/trainers` (registration) and `/api/login` (which authenticates as part of its own
  logic). A failed check throws `AuthenticationException`, which `GlobalExceptionHandler` turns into a `401` — Spring
  MVC routes exceptions thrown from `preHandle()` through the same `@ControllerAdvice` machinery as controller
  exceptions.
- **Logging:** `TransactionIdFilter` runs first, generating/propagating a transaction id via MDC and a response header;
  `RestLoggingInterceptor` runs next and logs each call's method, URI, status, and duration (including failed-auth
  attempts, since it's registered before `AuthenticationInterceptor`).

---

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.6+
- A Servlet 6-compatible container (e.g. Tomcat 10+) to deploy the WAR, or an embedded-servlet Maven plugin of your
  choice
- Docker (for the local PostgreSQL instance), or your own PostgreSQL server

### Start the database

```bash
docker compose up -d
```

This starts PostgreSQL on `localhost:5433` with database `gymcrm` / user `gymcrm` / password `gymcrm` (see
`docker-compose.yml`).

### Build

```bash
mvn clean install
```

This compiles the code, runs all tests, packages `gym-crm.war`, and generates a JaCoCo coverage report at
`target/site/jacoco/index.html`.

### Run

Deploy `target/gym-crm.war` to a Servlet 6 container (e.g. drop it in Tomcat 10+'s `webapps/`), or run it via your
IDE's/Maven's embedded-container plugin of choice. The API is then available under `/gym-crm/api/...` (context path
depends on how the WAR is deployed).

---

## Configuration

Database and Hibernate settings are configured in `src/main/resources/application.properties`:

```properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://localhost:5433/gymcrm
db.username=gymcrm
db.password=gymcrm
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
hibernate.format_sql=true
```

Point `db.url`/`db.driver`/credentials at any other JDBC-compatible database if needed.

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
```

---

## Testing

Run all tests:

```bash
mvn test
```

- Controller tests use MockMvc to exercise each endpoint's request/response contract and status codes
- Service tests mock DAO dependencies with Mockito, covering validation and username-collision handling
- DAO tests run against a real H2 `SessionFactory` with transaction rollback after each test
- Mapper, logging (`TransactionIdFilter`, `RestLoggingInterceptor`), security (`AuthenticationInterceptor`), and
  exception-handler behavior are covered separately
- Tests follow the **FIRST** principles (Fast, Isolated, Repeatable, Self-validating, Timely)
- Coverage is viewable in the JaCoCo report after `mvn clean install`

---

## Requirements Coverage

Checked against the REST task spec (`Task_Rest.pdf`):

| Area                                                                     | Status                                                            |
|--------------------------------------------------------------------------|-------------------------------------------------------------------|
| All 17 endpoints, correct HTTP methods & request/response shapes         | ✅                                                                 |
| Username/password generation                                             | ✅                                                                 |
| Can't register as both trainer and trainee                               | ✅ (enforced implicitly via global username uniqueness on `users`) |
| Users 1:1 with Trainer/Trainee; Trainee↔Trainer many-to-many             | ✅                                                                 |
| Training create-only via REST (no update/delete)                         | ✅                                                                 |
| Username immutable                                                       | ✅                                                                 |
| Field types (duration numeric, dates as `LocalDate`, `isActive` boolean) | ✅                                                                 |
| Training types constant, seeded, read-only via API                       | ✅                                                                 |
| Per-field request validation                                             | ✅                                                                 |
| Global error handling                                                    | ✅                                                                 |
| Hard delete of trainee cascades to trainings                             | ✅                                                                 |
| Activate/deactivate is non-idempotent (PATCH, not PUT)                   | ✅                                                                 |
| Two-level logging (transaction id + per-call)                            | ✅                                                                 |
| Unit test coverage                                                       | ✅                                                                 |
| Swagger 2 annotations on controller methods                              | ✅                                                                 |
| Auth required on every endpoint except registration                      | ✅                                                                 |

### Notes

- **Authentication enforcement** is implemented as `AuthenticationInterceptor`, a `HandlerInterceptor` registered for
  `/api/**` in `WebConfig`, requiring and verifying an HTTP Basic `Authorization` header via
  `AuthenticationService.authenticate(...)` on every call except `POST /api/trainees`, `POST /api/trainers` (
  registration), and `/api/login` (which is itself the credential check). It's unit-tested in isolation (
  `AuthenticationInterceptorTest`). Controller tests (`*ControllerTest`) build `MockMvc` with
  `standaloneSetup(controller)`, which doesn't wire `WebConfig`'s interceptors, so they exercise controller logic
  without needing Authorization headers — this is deliberate test isolation, not a gap in enforcement, since the
  interceptor itself is fully covered separately and is registered ahead of every controller in the real dispatch chain.
- The `GymFacade` and `Application.main` classes from the pre-REST module have been removed, since the REST controllers
  never used them and they were dead code.

---

## License

This project is provided for educational purposes.