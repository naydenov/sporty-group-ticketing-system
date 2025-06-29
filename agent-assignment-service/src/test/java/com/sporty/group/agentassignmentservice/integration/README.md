# Integration Tests for Agent Assignment Service

This directory contains integration tests for the Agent Assignment Service. These tests verify that the different components of the application work together correctly.

## Test Structure

The integration tests are organized into the following classes:

1. **AbstractIntegrationTest**: Base class for all integration tests. Sets up the test environment with:
   - Spring Boot application context
   - H2 in-memory database
   - Embedded Kafka broker

2. **AgentAssignmentIntegrationTest**: End-to-end tests for the agent assignment flow.
   - Tests the complete flow from consuming a NewTicket event to assigning an agent and producing a TicketAssigned event
   - Verifies that all components (Kafka, database, REST API) work together correctly

3. **ApiIntegrationTest**: Tests for the REST API endpoints.
   - Tests GET /api/agents - Returns all agents
   - Tests GET /api/agents/available - Returns only available agents
   - Tests GET /api/tickets - Returns all tickets
   - Tests GET /api/tickets/new - Returns only new tickets
   - Tests POST /api/tickets/{ticketId}/assign/{agentId} - Assigns an agent to a ticket
   - Tests error cases for the assignment endpoint

4. **KafkaIntegrationTest**: Tests for Kafka integration.
   - Tests consuming NewTicketEvent messages from Kafka
   - Tests producing TicketAssignedEvent messages to Kafka
   - Tests handling multiple ticket events
   - Tests handling invalid ticket events

## Running the Tests

### Using Maven Profiles

The project uses Maven profiles to control which tests are run:

- **Default Profile**: Excludes integration tests
  ```bash
  mvn test
  ```

- **Integration Test Profile**: Includes all tests (unit tests and integration tests)
  ```bash
  mvn test -P integration-test
  ```

### Running Specific Integration Tests

To run specific integration tests, use the following Maven commands:

```bash
# Run all integration tests
mvn test -P integration-test -Dtest=*IntegrationTest

# Run specific integration test classes
mvn test -P integration-test -Dtest=AgentAssignmentIntegrationTest
mvn test -P integration-test -Dtest=ApiIntegrationTest
mvn test -P integration-test -Dtest=KafkaIntegrationTest
```

## Test Configuration

The integration tests use a separate application properties file: `application-integration-test.properties`. This file configures:

- H2 in-memory database with create-drop mode for tests
- Kafka consumer and producer configuration

The Kafka bootstrap servers are configured to use the embedded Kafka broker provided by the @EmbeddedKafka annotation in the AbstractIntegrationTest class.

## Requirements

- Java 17 or higher
- Maven

## Notes

- Each test class cleans up the database before running tests to ensure a clean state
- The tests use random UUIDs for entities to avoid conflicts
- The embedded Kafka broker is automatically started and stopped by Spring Boot
