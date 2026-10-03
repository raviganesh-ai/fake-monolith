# Multi-stage Dockerfile for building and running the Spring Boot monolith on Azure Container Apps
# Build stage: use Maven to compile and package the application
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copy Maven descriptor and resolve dependencies first for better layer caching
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source and build the application
COPY src ./src
RUN mvn -B clean package -DskipTests

# Runtime stage: use a slim JRE image suitable for Azure Container Apps
FROM eclipse-temurin:17-jre-alpine

# Add labels for observability and documentation
LABEL org.opencontainers.image.title="fake-monolith" \
      org.opencontainers.image.description="Spring Boot monolith sample application" \
      org.opencontainers.image.source="https://github.com/raviganesh-ai/fake-monolith" \
      org.opencontainers.image.vendor="raviganesh-ai" \
      org.opencontainers.image.version="1.0.0"

# Create an application directory
WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /workspace/target/fake-monolith-*.jar /app/app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Use a non-root user for better security (azureuser uid 1000)
RUN addgroup -S azuregroup && adduser -S azureuser -G azuregroup
USER azureuser

# Configure Java runtime options via environment variable; Container Apps can override this
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Default command: run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
