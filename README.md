# Gym CRM System

A Spring Core-based module that handles a Gym CRM (Customer Relationship Management) system. It manages **Trainers**, **Trainees**, and **Trainings**, persisted with **Hibernate 6** on top of an H2 database.

---

## Features

- **Trainee management** – create, update, delete, activate/deactivate, and select trainee profiles
- **Trainer management** – create, update, activate/deactivate, and select trainer profiles
- **Training management** – add trainings, search trainee/trainer trainings with optional date, trainer/trainee name, and training type filters
- **Automatic username generation** – `firstName.lastName`, with a numeric suffix on duplicates (e.g. `John.Smith`, `John.Smith1`)
- **Automatic password generation** – random 10-character string
- **Authentication** – credential matching before every profile/training operation (except create)
- **Persistence with Hibernate** – entities mapped with JPA annotations, managed via a `SessionFactory`-backed generic DAO layer
- **Training type seeding** – default training types are seeded on startup if the table is empty
- **Logging** – SLF4J + Logback with appropriate log levels
- **Unit tested** – JUnit 5 + Mockito for services/facade, real Hibernate sessions against H2 for DAOs, with JaCoCo coverage (>90%)

---

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Context / ORM / TX / JDBC | 6.1.6 |
| Hibernate Core | 6.5.2.Final |
| H2 Database | 2.2.224 |
| HikariCP | 5.1.0 |
| Lombok | 1.18.38 |
| JUnit 5 | 5.10.2 |
| Mockito | 5.11.0 |
| Logback | 1.5.6 |
| JaCoCo | 0.8.12 |
| Maven | 3.6+ |

---

## Project Structure

```
gym-crm/
├── src/main/java/com/gym/crm/
│   ├── Application.java              # Entry point
│   ├── config/HibernateConfig.java   # DataSource, SessionFactory, TransactionManager
│   ├── domain/                       # Trainee, Trainer, Training, TrainingType, User (JPA entities)
│   ├── dao/                          # AbstractDao<T> + UserDao, TraineeDao, TrainerDao, TrainingDao, TrainingTypeDao
│   ├── service/                      # TraineeService, TrainerService, TrainingService, UserProfileService, AuthenticationService
│   ├── facade/GymFacade.java         # Single entry point aggregating services, authenticates before each call
│   ├── init/TrainingTypeSeeder.java  # Seeds default training types on startup
│   └── exception/                    # ValidationException, EntityNotFoundException, AuthenticationException
├── src/main/resources/
│   ├── application.properties        # DB connection + Hibernate properties
│   └── logback.xml                   # Logging configuration
└── src/test/java/com/gym/crm/        # Unit tests (services/facade mocked, DAOs against real H2 sessions)
```

---

## Architecture

- **Configuration:** Java-based `@Configuration` (`HibernateConfig`) with component scanning, `@EnableTransactionManagement`, and a `HikariDataSource` feeding a Hibernate `LocalSessionFactoryBean`.
- **Persistence:** Entities are mapped with JPA annotations (`@Entity`, `@OneToOne`, `@ManyToMany`, `@OneToMany`) and managed through `AbstractDao<T>`, a generic base DAO built on `SessionFactory#getCurrentSession()`, providing `save`, `update`, `findById`, `findAll`, and `delete`. Entity-specific DAOs add HQL/Criteria queries (username lookups, filtered training search, unassigned-trainer lookup).
- **Transactions:** Service methods run inside `@Transactional` boundaries (read-only where appropriate); Hibernate flushes and commits are handled by Spring's `HibernateTransactionManager`.
- **Dependency Injection:**
  - Services → `GymFacade` via **constructor** injection
  - DAOs → services via **constructor** injection (`@Autowired`)
  - Remaining dependencies (e.g. `UserProfileService`) via **setter** injection

---

## Getting Started

### Prerequisites
- JDK 17+
- Maven 3.6+

### Build

```bash
mvn clean install
```

This compiles the code, runs all tests, and generates a JaCoCo coverage report at `target/site/jacoco/index.html`.

### Run

**Option 1 – Maven exec plugin**
```bash
mvn exec:java -Dexec.mainClass="com.gym.crm.Application"
```

**Option 2 – From your IDE**
Run `com.gym.crm.Application`.
> **IntelliJ note:** If you see `ExceptionInInitializerError / TypeTag :: UNKNOWN`, enable annotation processing (Settings -> Build -> Compiler -> Annotation Processors) and do File -> Invalidate Caches / Restart.

---

## Configuration

Database and Hibernate settings are configured in `src/main/resources/application.properties`:

```properties
db.driver=org.h2.Driver
db.url=jdbc:h2:file:./data/gymcrm;AUTO_SERVER=TRUE
db.username=sa
db.password=

hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
hibernate.format_sql=true
```

By default the app uses a file-based H2 database (`./data/gymcrm`), so data persists between runs. Swap `db.url`/`db.driver`/credentials to point at any other JDBC-compatible database if needed.

---

## Usage Example

```java
GymFacade facade = context.getBean(GymFacade.class);

Trainee trainee = new Trainee();
User user = new User();
user.setFirstName("John");
user.setLastName("Smith");
trainee.setUser(user);
trainee.setAddress("Baker Street 221B");

Trainee saved = facade.createTraineeProfile(trainee);
// saved.getUser().getUsername() -> "John.Smith"
// saved.getUser().getPassword() -> random 10-char string

Trainee profile = facade.getTraineeProfile("John.Smith", saved.getUser().getPassword());
```

---

## Testing

Run all tests:
```bash
mvn test
```

- Service and facade tests mock DAO dependencies with Mockito, covering validation, authentication checks, and username-collision handling
- DAO tests run against a real in-memory H2 `SessionFactory` with transaction rollback after each test
- Tests follow the **FIRST** principles (Fast, Isolated, Repeatable, Self-validating, Timely)
- Line/branch coverage exceeds **90%** (viewable in the JaCoCo report)

---

## License

This project is provided for educational purposes.