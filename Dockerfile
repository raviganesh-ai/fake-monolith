# Multi-stage Dockerfile for the Fake Monolith Spring Boot application
# Stage 1: Build the application using Maven
FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copy Maven descriptor and source code
COPY pom.xml .
COPY src ./src

# Build the application jar (skip tests can be toggled via build pipeline if desired)
RUN mvn -B package -DskipTests

# Stage 2: Runtime image for Azure Container Apps
FROM eclipse-temurin:17-jre

# Create app directory
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /workspace/target/monolith-*.jar /app/app.jar

# Use a non-root user for better security posture in container environments
RUN useradd -u 1001 springapp \
    && chown -R springapp:springapp /app
USER springapp

# Expose the default Spring Boot port
EXPOSE 8080

# Environment variables can be overridden by Azure Container Apps configuration
ENV SERVER_PORT=8080 \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE="default"

# Health check using Spring Boot Actuator endpoint (enabled via application.properties)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Entrypoint to run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
