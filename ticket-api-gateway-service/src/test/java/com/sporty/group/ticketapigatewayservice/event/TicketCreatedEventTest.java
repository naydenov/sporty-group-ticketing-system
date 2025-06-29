package com.sporty.group.ticketapigatewayservice.event;

import com.sporty.group.ticketapigatewayservice.model.Ticket;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketCreatedEventTest {

    @Test
    public void testTicketCreatedEventBuilder() {
        // Create test data
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-001";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Build an event using the builder
        TicketCreatedEvent event = TicketCreatedEvent.builder()
                .ticketId(ticketId)
                .subject(subject)
                .description(description)
                .status(status)
                .userId(userId)
                .assigneeId(assigneeId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Verify all fields are set correctly
        assertEquals(ticketId, event.getTicketId());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
        assertEquals(status, event.getStatus());
        assertEquals(userId, event.getUserId());
        assertEquals(assigneeId, event.getAssigneeId());
        assertEquals(createdAt, event.getCreatedAt());
        assertEquals(updatedAt, event.getUpdatedAt());
    }

    @Test
    public void testTicketCreatedEventNoArgsConstructor() {
        // Create an event using the no-args constructor
        TicketCreatedEvent event = new TicketCreatedEvent();

        // Verify all fields are null
        assertNull(event.getTicketId());
        assertNull(event.getSubject());
        assertNull(event.getDescription());
        assertNull(event.getStatus());
        assertNull(event.getUserId());
        assertNull(event.getAssigneeId());
        assertNull(event.getCreatedAt());
        assertNull(event.getUpdatedAt());
    }

    @Test
    public void testTicketCreatedEventAllArgsConstructor() {
        // Create test data
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-001";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Create an event using the all-args constructor
        TicketCreatedEvent event = new TicketCreatedEvent(ticketId, subject, description, status, userId, assigneeId, createdAt, updatedAt);

        // Verify all fields are set correctly
        assertEquals(ticketId, event.getTicketId());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
        assertEquals(status, event.getStatus());
        assertEquals(userId, event.getUserId());
        assertEquals(assigneeId, event.getAssigneeId());
        assertEquals(createdAt, event.getCreatedAt());
        assertEquals(updatedAt, event.getUpdatedAt());
    }

    @Test
    public void testTicketCreatedEventSettersAndGetters() {
        // Create an event using the no-args constructor
        TicketCreatedEvent event = new TicketCreatedEvent();

        // Create test data
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-001";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Set the fields using setters
        event.setTicketId(ticketId);
        event.setSubject(subject);
        event.setDescription(description);
        event.setStatus(status);
        event.setUserId(userId);
        event.setAssigneeId(assigneeId);
        event.setCreatedAt(createdAt);
        event.setUpdatedAt(updatedAt);

        // Verify all fields are set correctly using getters
        assertEquals(ticketId, event.getTicketId());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
        assertEquals(status, event.getStatus());
        assertEquals(userId, event.getUserId());
        assertEquals(assigneeId, event.getAssigneeId());
        assertEquals(createdAt, event.getCreatedAt());
        assertEquals(updatedAt, event.getUpdatedAt());
    }

    @Test
    public void testFromTicket() {
        // Create a ticket
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-001";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .ticketId(ticketId)
                .subject(subject)
                .description(description)
                .status(status)
                .userId(userId)
                .assigneeId(assigneeId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Create an event from the ticket
        TicketCreatedEvent event = TicketCreatedEvent.fromTicket(ticket);

        // Verify all fields are copied correctly
        assertEquals(ticket.getTicketId(), event.getTicketId());
        assertEquals(ticket.getSubject(), event.getSubject());
        assertEquals(ticket.getDescription(), event.getDescription());
        assertEquals(ticket.getStatus(), event.getStatus());
        assertEquals(ticket.getUserId(), event.getUserId());
        assertEquals(ticket.getAssigneeId(), event.getAssigneeId());
        assertEquals(ticket.getCreatedAt(), event.getCreatedAt());
        assertEquals(ticket.getUpdatedAt(), event.getUpdatedAt());
    }
}