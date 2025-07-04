# syntax=docker/dockerfile:1

# This Dockerfile is designed for a multi-module Maven project where:
# 1. The 'docker-compose.yaml' service's build context is the project root (e.g., 'context: .').
# 2. The parent pom.xml, mvnw, and .mvn directory are in the project root.
# 3. This Dockerfile is located in the specific module's directory (e.g., 'sporty-group-ticketing-system/ticket-api-gateway-service/Dockerfile').

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

# Copy the built JAR of the common module into the Maven local repository
COPY --from=build /app/sporty-group-ticketing-commons/target/sporty-group-ticketing-commons-*.jar \
     /root/.m2/repository/com/sporty/group/sporty-group-ticketing-commons/0.0.1-SNAPSHOT/sporty-group-ticketing-commons-0.0.1-SNAPSHOT.jar

# --- IMPORTANT CHANGE HERE ---
# Copy ALL module directories into the build context inside the container.
# This is crucial for Maven to correctly resolve the multi-module project structure
# when running commands like 'dependency:go-offline' on the parent POM,
# as it needs all declared modules to be present.
COPY --link ticket-api-gateway-service ./ticket-api-gateway-service

# Run 'dependency:go-offline' on the parent project.
# This downloads all project dependencies (including those managed by the parent)
# into the local Maven repository within the container, leveraging Docker's layer caching.
# '-f pom.xml' ensures the command is executed against the parent POM.
RUN ./mvnw dependency:go-offline -B

# --- IMPORTANT CHANGE HERE ---
# Instead of copying pom.xml and src separately for the module,
# copy the entire module directory. This ensures all internal relative paths
# (like pom.xml to src) are correctly preserved for Maven.
COPY --link ticket-api-gateway-service ./ticket-api-gateway-service

# Build the specific application module.
# 'clean install': Cleans the project and installs the artifacts into the local Maven repository.
# '-DskipTests': Skips running tests for faster build times in Docker.
# '-pl ticket-api-gateway-service': Specifies that only this module should be built.
# '-am': Also builds any modules that 'ticket-api-gateway-service' depends on within the multi-module project.
# '-f pom.xml': Ensures Maven uses the parent pom.xml as the entry point for the build.
RUN ./mvnw clean install -DskipTests -pl ticket-api-gateway-service -am -f pom.xml

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
# The wildcard '*' is used to copy the generated JAR file (e.g., 'ticket-api-gateway-service-0.0.1-SNAPSHOT.jar').
COPY --from=build /app/ticket-api-gateway-service/target/*.jar /app/app.jar

# Set ownership of the application files to the non-root user/group.
RUN chown -R appuser:appgroup /app
USER appuser

# Expose the port on which the Spring Boot application will listen.
# This informs Docker that the container listens on this port.
EXPOSE 8080

# Set environment variables with sensible defaults.
# These can be overridden when running the container (e.g., with 'docker run -e').
ENV SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8080 \
    SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Set JVM options for container awareness and performance.
# -XX:MaxRAMPercentage: Allows the JVM to use a percentage of the container's memory.
# -Djava.security.egd: Improves entropy source for secure random number generation.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -Djava.security.egd=file:/dev/./urandom"

# Define the command to run the application when the container starts.
# 'sh -c exec java ...' ensures proper signal handling, allowing graceful shutdown.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
