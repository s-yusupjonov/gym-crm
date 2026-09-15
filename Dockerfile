## --- Build stage: compile & package the executable jar ---
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

## --- Runtime stage: plain JRE running the Spring Boot fat jar ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/gym-crm.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]