# Ticket Management Service - Project Overview

## Introduction
The Ticket Management Service is a Spring Boot application designed to handle support ticket operations through an asynchronous messaging approach using Kafka. It processes various ticket-related events, updates ticket information in a database, and publishes events to notify other services about ticket changes.

## Architecture
The service follows an event-driven architecture with the following components:

1. **Event Listeners**: Listen for ticket-related events from Kafka topics
2. **Service Layer**: Processes events and performs business logic
3. **Repository Layer**: Handles data persistence
4. **Event Publishers**: Publishes events to Kafka topics

## Key Features
- Processing ticket creation, assignment, and status updates
- Asynchronous communication via Kafka
- In-memory database storage for ticket information

## Data Model

### Ticket Entity
- **ticketId** (UUID): Unique identifier for the ticket
- **subject**: Brief description of the ticket
- **description**: Detailed description of the issue
- **status**: Current status of the ticket (open, in_progress, resolved, closed)
- **userId**: ID of the user who created the ticket
- **assigneeId**: ID of the agent assigned to the ticket (nullable)
- **createdAt/updatedAt**: Timestamps for creation and last update

## Event Types

### 1. TicketCreated
Received when a new ticket is created by a user.
```json
{
  "userId": "user-001",
  "subject": "Login problem",
  "description": "Cannot reset my password"
}
```

### 2. TicketAssigned
Received when a ticket is assigned to an agent.
```json
{
  "ticketId": "abc123",
  "assigneeId": "agent-007"
}
```

### 3. TicketStatusUpdated
Received when a ticket's status is updated.
```json
{
  "ticketId": "abc123",
  "status": "in_progress"
}
```

### 4. NewTicket
Published when a new ticket is created in the system.
```json
{
  "ticketId": "abc123",
  "status": "open",
  "subject": "Login problem",
  "description": "Cannot reset my password",
  "createdAt": "15.03.2025"
}
```

## Kafka Configuration
The service is configured to work with the following Kafka topics:
- **support-tickets**: Receives TicketCreated events
- **ticket-assignments**: Receives TicketAssigned events
- **ticket-updates**: Receives TicketStatusUpdated events
- **ticket-created**: Publishes NewTicket events

## Deployment
The service is containerized using Docker and can be deployed using Docker Compose, which sets up:
- The Ticket Management Service
- Kafka broker
- Zookeeper (required for Kafka)

## Development Guidelines
- Follow Spring Boot best practices
- Ensure proper error handling and logging
- Write unit tests for all components
- Document code with clear comments and Javadoc