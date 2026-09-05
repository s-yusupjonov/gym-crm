## --- Build stage: compile & package the WAR ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Pre-fetch dependencies into a layer that's cached unless pom.xml changes
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

## --- Runtime stage: Tomcat 10.1 (Servlet 6, required by jakarta.servlet-api 6.0.0) ---
FROM tomcat:10.1-jdk17-temurin
# Remove Tomcat's default ROOT app so it doesn't shadow anything
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/gym-crm.war /usr/local/tomcat/webapps/gym-crm.war
EXPOSE 8080
CMD ["catalina.sh", "run"]