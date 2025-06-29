# Sporty Group Ticketing System

This project is a microservices-based ticketing system for managing support tickets, agent assignments, and ticket status updates.

## Setup and Run Instructions

### Prerequisites
- **Docker and Docker Compose:** Required for containerized deployment
- **Java 17:** Required for local development (Eclipse Temurin JDK recommended)
- **Maven:** Required for building the project locally

### Project-Specific Requirements
- **Java Version:** All services require Java 17 (Eclipse Temurin base images).
- **Kafka & Zookeeper:** Kafka (on port 9092) and Zookeeper (on port 2181) are required and included in the `docker-compose.yml`.
- **H2 Database:** Each service uses an in-memory H2 database (no persistent storage required).

### Environment Variables
Each service sets sensible defaults for environment variables, which can be overridden at runtime:

- **Ticket API Gateway Service**
  - `SPRING_PROFILES_ACTIVE` (default: `docker`)
  - `SERVER_PORT` (default: `8080`)
  - `SPRING_KAFKA_BOOTSTRAP_SERVERS` (default: `kafka:9092`)

- **Ticket Management Service**
  - `SPRING_PROFILES_ACTIVE` (default: `docker`)
  - `SERVER_PORT` (default: `8081`)
  - `SPRING_KAFKA_BOOTSTRAP_SERVERS` (default: `kafka:9092`)
  - `SPRING_DATASOURCE_URL` (default: `jdbc:h2:mem:ticketdb`)
  - `SPRING_DATASOURCE_USERNAME` (default: `sa`)
  - `SPRING_DATASOURCE_PASSWORD` (default: `password`)

- **Agent Assignment Service**
  - `SPRING_PROFILES_ACTIVE` (default: `docker`)
  - `SERVER_PORT` (default: `8082`)
  - `SPRING_KAFKA_BOOTSTRAP_SERVERS` (default: `kafka:9092`)
  - `SPRING_DATASOURCE_URL` (default: `jdbc:h2:mem:agents-db`)
  - `SPRING_DATASOURCE_USERNAME` (default: `sa`)
  - `SPRING_DATASOURCE_PASSWORD` (default: empty)

You can override these by creating `.env` files in each service directory and uncommenting the `env_file` lines in `docker-compose.yml`.

### Exposed Ports
- **Ticket API Gateway Service:** 8080
- **Ticket Management Service:** 8081
- **Agent Assignment Service:** 8082
- **Kafka:** 9092
- **Zookeeper:** 2181

### Building and Running the Project

#### Using Docker Compose (Recommended)
To build and start all services, simply run:

```bash
docker-compose up -d
```

This will build the Docker images for each service (using the multi-stage Dockerfiles) and start all containers, including Kafka and Zookeeper. All services are connected via the `app-net` Docker network for internal communication.

To stop and remove all containers:

```bash
docker-compose down
```

To remove containers and associated volumes:

```bash
docker-compose down -v
```

#### Running Locally for Development
1. Start Kafka and Zookeeper locally or using Docker Compose:
   ```bash
   docker-compose up -d kafka zookeeper
   ```

2. For each service, navigate to its directory and run:
   ```bash
   ./mvnw spring-boot:run
   ```

## Message Formats Used

The system uses Kafka for asynchronous communication between services. The following message formats are used:

### Ticket Created Event
Published by the Ticket API Gateway Service when a new ticket is created:
```json
{
  "ticketId": "uuid-string",
  "subject": "Login problem",
  "description": "Cannot reset my password",
  "status": "OPEN",
  "userId": "user-001",
  "assigneeId": null,
  "createdAt": "2023-05-15T10:30:00",
  "updatedAt": "2023-05-15T10:30:00"
}
```

### Ticket Assigned Event
Published by the Agent Assignment Service when a ticket is assigned to an agent:
```json
{
  "ticketId": "uuid-string",
  "assigneeId": "agent-uuid-string"
}
```

### Ticket Status Updated Event
Published when a ticket's status is updated:
```json
{
  "ticketId": "uuid-string",
  "status": "IN_PROGRESS"
}
```

## Tests Included

The project includes both unit tests and integration tests for each service:

### Unit Tests
- Controller tests using MockMvc
- Service layer tests with Mockito
- Repository tests with H2 in-memory database
- Event serialization/deserialization tests

### Integration Tests
- End-to-end tests using Testcontainers for Kafka
- API endpoint tests
- Event processing tests
- Database interaction tests

To run only unit tests:
```bash
mvn test
```

To run both unit and integration tests:
```bash
mvn test -P integration-test
```

## AI Tool Usage and Validation

This project does not currently use AI tools for code generation or validation. All code is manually written and tested.

### Validation Steps
1. Unit tests verify individual components
2. Integration tests verify service interactions
3. Manual testing with Swagger UI
4. Docker Compose ensures consistent deployment environment

### Future AI Integration Possibilities
- Ticket categorization and routing
- Automated agent assignment based on skills and workload
- Sentiment analysis for customer feedback
- Anomaly detection for system monitoring
