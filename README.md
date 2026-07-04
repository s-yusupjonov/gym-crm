# Gym CRM System

A Spring Core-based module that handles a Gym CRM (Customer Relationship Management) system. It manages **Trainers**, **Trainees**, and **Trainings** using an in-memory storage implemented as Spring beans.

---

## Features

- **Trainee management** – create, update, delete, and select trainee profiles
- **Trainer management** – create, update, and select trainer profiles
- **Training management** – create and select trainings
- **Automatic username generation** – `firstName.lastName`, with a numeric suffix on duplicates (e.g. `John.Smith`, `John.Smith1`)
- **Automatic password generation** – random 10-character string
- **In-memory storage** – separate `Map` bean per entity type
- **Data preloading** – storage is initialized from external CSV files at startup
- **Logging** – SLF4J + Logback with appropriate log levels
- **Unit tested** – JUnit 5 + Mockito with JaCoCo coverage (>80%)

---

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Context | 6.1.6 |
| Lombok | 1.18.38 |
| JUnit 5 | 5.10.2 |
| Mockito | 5.11.0 |
| Logback | 1.5.6 |
| JaCoCo | 0.8.12 |
| Maven | 3.6+ |

---

## Project Structure

\`\`\`
gym-crm/
├── src/main/java/com/gym/crm/
│   ├── Application.java              # Entry point
│   ├── config/AppConfig.java         # Java-based Spring configuration
│   ├── domain/                       # Trainee, Trainer, Training, User, TrainingType
│   ├── storage/                      # InMemoryStorage + StorageInitializer (BeanPostProcessor)
│   ├── dao/                          # TraineeDao, TrainerDao, TrainingDao
│   ├── service/                      # TraineeService, TrainerService, TrainingService, UserProfileService
│   └── facade/GymFacade.java         # Single entry point aggregating services
├── src/main/resources/
│   ├── application.properties        # Externalized file paths
│   ├── logback.xml                   # Logging configuration
│   ├── trainees.csv                  # Preloaded trainee data
│   ├── trainers.csv                  # Preloaded trainer data
│   └── trainings.csv                 # Preloaded training data
└── src/test/java/com/gym/crm/        # Unit tests
\`\`\`

---

## Architecture

- **Configuration:** Java-based \`@Configuration\` with component scanning.
- **Storage:** Each entity has its own \`Map\` Spring bean, grouped inside \`InMemoryStorage\`. Data is loaded from external CSV files by \`StorageInitializer\`, a \`BeanPostProcessor\`, using paths from property placeholders.
- **Dependency Injection:**
    - Services → \`GymFacade\` via **constructor** injection
    - DAO/storage → services via **constructor** injection (\`@Autowired\`)
    - Remaining dependencies (e.g. \`UserProfileService\`) via **setter** injection

---

## Getting Started

### Prerequisites
- JDK 17+
- Maven 3.6+

### Build

\`\`\`bash
mvn clean install
\`\`\`

This compiles the code, runs all tests, and generates a JaCoCo coverage report at \`target/site/jacoco/index.html\`.

### Run

**Option 1 – Maven exec plugin**
\`\`\`bash
mvn exec:java -Dexec.mainClass="com.gym.crm.Application"
\`\`\`

**Option 2 – From your IDE**
Run \`com.gym.crm.Application\`.
> **IntelliJ note:** If you see \`ExceptionInInitializerError / TypeTag :: UNKNOWN\`, enable annotation processing (Settings -> Build -> Compiler -> Annotation Processors) and do File -> Invalidate Caches / Restart.

---

## Configuration

Data file paths are configured in \`src/main/resources/application.properties\`:

\`\`\`properties
storage.trainees.file=trainees.csv
storage.trainers.file=trainers.csv
storage.trainings.file=trainings.csv
\`\`\`

### Sample data format

**trainees.csv**
\`\`\`csv
id,firstName,lastName,username,password,isActive,dateOfBirth,address
1,Peter,Parker,Peter.Parker,abc1234567,true,1995-08-10,New York
\`\`\`

**trainers.csv**
\`\`\`csv
id,firstName,lastName,username,password,isActive,specialization
1,Bruce,Wayne,Bruce.Wayne,pass111222,true,STRENGTH
\`\`\`

**trainings.csv**
\`\`\`csv
id,traineeId,trainerId,trainingName,trainingType,trainingDate,duration
1,1,1,Morning Strength,STRENGTH,2024-01-15,60
\`\`\`

---

## Usage Example

\`\`\`java
GymFacade facade = context.getBean(GymFacade.class);

Trainee trainee = new Trainee();
trainee.setFirstName("John");
trainee.setLastName("Smith");
trainee.setAddress("Baker Street 221B");

Trainee saved = facade.createTrainee(trainee);
// saved.getUsername() -> "John.Smith"
// saved.getPassword() -> random 10-char string
\`\`\`

---

## Testing

Run all tests:
\`\`\`bash
mvn test
\`\`\`

- Unit tests cover services and DAOs using JUnit 5 + Mockito
- Tests follow the **FIRST** principles (Fast, Isolated, Repeatable, Self-validating, Timely)
- Line coverage exceeds **80%** (viewable in the JaCoCo report)

---

## License

This project is provided for educational purposes.
EOF