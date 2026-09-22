## --- Build stage: compile & package the executable jar ---
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

## --- Runtime stage: plain JRE running the Spring Boot fat jar (as a non-root user) ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN useradd --system --create-home appuser
COPY --from=build /app/target/gym-crm.jar app.jar
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=3s --start-period=60s --retries=5 \
  CMD wget -qO- "http://localhost:${SERVER_PORT:-8080}/gym-crm/actuator/health" > /dev/null || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
