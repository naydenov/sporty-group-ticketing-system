# syntax=docker/dockerfile:1

# This Dockerfile is designed for a multi-module Maven project where:
# 1. The 'docker-compose.yaml' service's build context is the project root (e.g., 'context: .').
# 2. The parent pom.xml, mvnw, and .mvn directory are in the project root.
# 3. This Dockerfile is located in the specific module's directory (e.g., 'sporty-group-ticketing-system/agent-assignment-service/Dockerfile').

# --- Build stage ---
# Use a Java Development Kit (JDK) image for building the application.
# eclipse-temurin:17-jdk provides OpenJDK 17.
FROM eclipse-temurin:17-jdk AS build

# Set the working directory inside the container for the build process.
# All subsequent commands will be executed relative to this directory.
WORKDIR /app

# Copy Maven wrapper scripts and configuration files from the build context root.
# This allows Maven to be executed within the container.
COPY --link mvnw ./mvnw
COPY --link .mvn ./.mvn/

# Copy the parent project's pom.xml from the build context root.
# This is crucial for Maven to understand the multi-module structure and resolve parent dependencies.
COPY --link pom.xml ./

# Give execution rights to the Maven wrapper script.
RUN chmod +x mvnw

# Copy ALL module directories into the build context inside the container.
# This is crucial for Maven to correctly resolve the multi-module project structure
# when running commands like 'dependency:go-offline' on the parent POM,
# as it needs all declared modules to be present.
COPY --link agent-assignment-service ./agent-assignment-service
COPY --link ticket-api-gateway-service ./ticket-api-gateway-service
COPY --link ticket-management-service ./ticket-management-service

# Run 'dependency:go-offline' on the parent project.
# Now that all modules are present, this command should succeed in building the project model.
# This downloads all project dependencies (including those managed by the parent)
# into the local Maven repository within the container, leveraging Docker's layer caching.
# '-f pom.xml' ensures the command is executed against the parent POM.
RUN ./mvnw dependency:go-offline -f pom.xml

# Build the specific application module.
# 'clean install': Cleans the project and installs the artifacts into the local Maven repository.
# '-DskipTests': Skips running tests for faster build times in Docker.
# '-pl agent-assignment-service': Specifies that only this module should be built.
# '-am': Also builds any modules that 'agent-assignment-service' depends on within the multi-module project.
# '-f pom.xml': Ensures Maven uses the parent pom.xml as the entry point for the build.
RUN ./mvnw clean install -DskipTests -pl agent-assignment-service -am -f pom.xml

# --- Runtime stage ---
# Use a Java Runtime Environment (JRE) image, which is smaller and more suitable for running the application.
FROM eclipse-temurin:17-jre

# Set the working directory inside the container for the runtime.
WORKDIR /app

# Create a non-root user and group for security best practices.
# '--system' creates system users/groups, which are typically used for services.
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy the built JAR from the 'build' stage into the runtime image.
# The JAR is located in the module's 'target' directory within the build stage.
# The wildcard '*' is used to copy the generated JAR file (e.g., 'agent-assignment-service-0.0.1-SNAPSHOT.jar').
COPY --from=build /app/agent-assignment-service/target/*.jar /app/app.jar

# Set ownership of the application files to the non-root user/group.
RUN chown -R appuser:appgroup /app
USER appuser

# Expose the service port
EXPOSE 8082

# Set environment variables with sensible defaults (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8082 \
    SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
    SPRING_DATASOURCE_URL=jdbc:h2:mem:agents-db \
    SPRING_DATASOURCE_USERNAME=sa \
    SPRING_DATASOURCE_PASSWORD=

# JVM options for container awareness and memory limits
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -Djava.security.egd=file:/dev/./urandom"

# Use exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]