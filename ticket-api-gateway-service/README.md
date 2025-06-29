# Ticket API Gateway Service

This service acts as the entry point for creating support tickets in the system. It exposes a REST API for ticket creation and publishes events to Kafka for processing by other services.

## Features

- Exposes REST API for ticket creation
- Validates ticket request data
- Generates unique ticket IDs
- Publishes ticket creation events to Kafka
- Includes Swagger/OpenAPI documentation

## Setup and Run Instructions

### Prerequisites

- Java 17
- Maven
- Kafka (running on localhost:9092)
- Docker (for running with Docker Compose or integration tests)

### Running Locally

1. Start Kafka locally or use Docker Compose:
   ```bash
   # From the project root
   docker-compose up -d kafka zookeeper
   ```

2. Build the application:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Running with Docker

The service can be run as part of the complete system using Docker Compose from the project root:

```bash
docker-compose up -d
```

## API Endpoints

### Tickets

- `POST /api/v1/tickets` - Create a new ticket

Example request body:
```json
{
  "userId": "user-001",
  "subject": "Login problem",
  "description": "Cannot reset my password"
}
```

## Message Formats Used

### Producing Events

The service produces events to the `support-tickets` Kafka topic with the following format:

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

### Kafka Topics
- `support-tickets`: For new ticket creation events (produced)

## Tests Included

### Unit Tests
- Controller tests using MockMvc
- Service layer tests with Mockito
- Event serialization/deserialization tests

### Integration Tests
Integration tests use Testcontainers to spin up a Kafka container for testing the event publishing functionality:
- End-to-end tests for the ticket creation flow
- Tests for Kafka integration using Testcontainers

To run tests:
```bash
mvn test
```

## API Documentation

This service includes Swagger/OpenAPI documentation for the REST API.

### Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

The Swagger UI provides:
- Interactive documentation for all API endpoints
- The ability to test API calls directly from the browser
- Detailed request and response schemas

### OpenAPI Specification

The OpenAPI specification in JSON format is available at:

```
http://localhost:8080/api-docs
```

You can import this JSON into tools like Postman for API testing.

### Validation Steps
1. Unit tests verify individual components
2. Integration tests verify service interactions with Kafka
3. Manual testing with Swagger UI
4. Docker Compose ensures consistent deployment environment
