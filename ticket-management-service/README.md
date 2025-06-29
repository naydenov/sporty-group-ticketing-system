# Ticket Management Service

This service manages support tickets, handling their creation, assignment, and status updates through Kafka events.

## Features

- Process ticket creation events
- Handle ticket assignment to support agents
- Process ticket status updates
- Communicate with other services via Kafka
- RESTful API with Swagger documentation

## Setup and Run Instructions

### Prerequisites

- Java 17
- Maven
- Docker (for running integration tests)
- Kafka (for local development)

### Running Locally

1. Start Kafka locally or use Docker Compose:
   ```bash
   # From the project root
   docker-compose up -d kafka zookeeper
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Running with Docker

The service can be run as part of the complete system using Docker Compose from the project root:

```bash
docker-compose up -d
```

## Message Formats Used

This service processes and produces the following Kafka message formats:

### Ticket Created Event (Consumed)
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

### Ticket Assigned Event (Consumed)
```json
{
  "ticketId": "uuid-string",
  "assigneeId": "agent-uuid-string"
}
```

### Ticket Status Updated Event (Consumed)
```json
{
  "ticketId": "uuid-string",
  "status": "IN_PROGRESS"
}
```

### Kafka Topics
- `support-tickets`: For new ticket creation events
- `ticket-assignments`: For ticket assignment events
- `ticket-updates`: For ticket status update events

## Tests Included

The project uses Maven for building and testing. It's configured with two profiles to control whether integration tests are run:

### Unit Tests

By default, only unit tests are run, excluding integration tests:

```bash
mvn clean install
```

or explicitly:

```bash
mvn clean install -P default
```

Unit tests cover:
- Service layer logic
- Event processing
- Data validation
- Repository interactions

### Integration Tests

To run both unit tests and integration tests:

```bash
mvn clean install -P integration-test
```

Integration tests are located in the `src/test/java/com/sporty/group/ticketmanagementservice/integration` package. These tests:

- Use Testcontainers to spin up Kafka infrastructure
- Test the full flow of ticket creation, assignment, and status updates
- Verify the system's behavior with external dependencies

Integration tests are excluded from the default build to speed up development cycles, but should be run before deploying to ensure the system works correctly with its dependencies.

## API Documentation

This service includes Swagger/OpenAPI documentation for the REST API.

### Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8081/swagger-ui.html
```

The Swagger UI provides:
- Interactive documentation for all API endpoints
- The ability to test API calls directly from the browser
- Detailed request and response schemas
- Authentication requirements (if configured)

### OpenAPI Specification

The OpenAPI specification in JSON format is available at:

```
http://localhost:8081/api-docs
```

You can import this JSON into tools like Postman for API testing.

## AI Tool Usage and Validation

This service does not currently use AI tools for code generation or validation. All code is manually written and tested.

### Validation Steps
1. Unit tests verify individual components
2. Integration tests verify service interactions with Kafka
3. Manual testing with Swagger UI
4. Docker Compose ensures consistent deployment environment

### Future AI Integration Possibilities
- Ticket categorization based on content
- Priority assignment based on text analysis
- Automated response suggestions
