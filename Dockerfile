# ---- Build stage: compile and package with Maven + JDK 11 (off the host) ----
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline
COPY src ./src
RUN mvn -q -e -B -DskipTests package

# ---- Runtime stage: run the packaged jar on a Java 11 JRE as a non-root user ----
FROM eclipse-temurin:11-jre
WORKDIR /app
RUN useradd --system --uid 1001 appuser
COPY --from=build /app/target/fake-monolith.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
