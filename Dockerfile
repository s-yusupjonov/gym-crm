## --- Build stage: compile & package the executable jar ---
FROM --platform=linux/amd64 maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Pre-fetch dependencies into a layer that's cached unless pom.xml changes
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

## --- Runtime stage: plain JRE running the Spring Boot fat jar ---
FROM --platform=linux/amd64 eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/gym-crm.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
