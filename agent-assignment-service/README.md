# Agent Assignment Service

A service for managing ticket assignments to agents using an asynchronous messaging approach with Kafka.

## Features

- Consumes NewTicket events from Kafka topic `ticket-created`
- Stores tickets and agents in an H2 in-memory database
- Exposes APIs for listing all new tickets
- Exposes APIs for listing all available agents
- Exposes API for assigning an agent to a ticket
- Produces TicketAssigned events to Kafka topic `ticket-assignments`

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

### Agents

- `GET /api/agents` - Get all agents
- `GET /api/agents/available` - Get all available agents

### Tickets

- `GET /api/tickets` - Get all tickets
- `GET /api/tickets/new` - Get all new (open) tickets
- `POST /api/tickets/{ticketId}/assign/{agentId}` - Assign an agent to a ticket

## Message Formats Used

### Consuming Events

The service consumes events from the `ticket-created` topic with the following format:

```json
{
  "ticketId": "abc123",
  "userId": "abc123",
  "status": "open",
  "subject": "Login problem",
  "description": "Cannot reset my password",
  "createdAt": "15.03.2025"
}
```

### Producing Events

When an agent is assigned to a ticket, the service produces an event to the `ticket-assignments` topic with the following format:

```json
{
  "ticketId": "abc123",
  "assigneeId": "agent-007"
}
```

### Kafka Topics
- `ticket-created`: For new ticket creation events (consumed)
- `ticket-assignments`: For ticket assignment events (produced)

## Tests Included

### Running Tests

The project uses Maven profiles to control which tests are run:

- **Default Profile**: Excludes integration tests
  ```bash
  mvn test
  ```

- **Integration Test Profile**: Includes all tests (unit tests and integration tests)
  ```bash
  mvn test -P integration-test
  ```

### Unit Tests
- Controller tests using MockMvc
- Service layer tests with Mockito
- Repository tests with H2 in-memory database
- Event serialization/deserialization tests

### Integration Tests
Integration tests are located in the `src/test/java/com/sporty/group/agentassignmentservice/integration` package and include:
- End-to-end tests for the agent assignment flow
- Tests for Kafka integration using Testcontainers
- Tests for REST API endpoints

### Sample Data

The application initializes with sample data:

- Three agents with different skills and availability statuses
- Agent IDs:
  - Available: `11111111-1111-1111-1111-111111111111` (John Doe)
  - Available: `22222222-2222-2222-2222-222222222222` (Jane Smith)
  - Not Available: `33333333-3333-3333-3333-333333333333` (Bob Johnson)

### Testing with Kafka

To test the Kafka integration, you can use the Kafka console producer to send a NewTicket event:

```bash
kafka-console-producer.sh --broker-list localhost:9092 --topic ticket-created
```

Then paste the following JSON:

```json
{
  "ticketId": "12345678-1234-1234-1234-123456789012",
  "userId": "user123",
  "status": "open",
  "subject": "Test Ticket",
  "description": "This is a test ticket",
  "createdAt": "15.03.2025"
}
```

You can then use the API to assign an agent to this ticket and check the `ticket-assignments` topic for the produced event.

## H2 Console

The H2 console is enabled and available at: http://localhost:8082/h2-console

- JDBC URL: `jdbc:h2:mem:agents-db`
- Username: `sa`
- Password: (empty)

## AI Tool Usage and Validation

This service does not currently use AI tools for code generation or validation. All code is manually written and tested.

### Validation Steps
1. Unit tests verify individual components
2. Integration tests verify service interactions with Kafka
3. Manual testing with API endpoints
4. Docker Compose ensures consistent deployment environment

### Future AI Integration Possibilities
- Intelligent agent assignment based on ticket content and agent skills
- Workload balancing using predictive analytics
- Agent performance monitoring and optimization
