package com.sporty.group.ticketapigatewayservice.model;

import com.sporty.group.sportygroupticketingcommons.model.Ticket;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketTest {

    @Test
    public void testTicketBuilder() {
        // Create test data
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-001";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Build a ticket using the builder
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

        // Verify all fields are set correctly
        assertEquals(ticketId, ticket.getTicketId());
        assertEquals(subject, ticket.getSubject());
        assertEquals(description, ticket.getDescription());
        assertEquals(status, ticket.getStatus());
        assertEquals(userId, ticket.getUserId());
        assertEquals(assigneeId, ticket.getAssigneeId());
        assertEquals(createdAt, ticket.getCreatedAt());
        assertEquals(updatedAt, ticket.getUpdatedAt());
    }

    @Test
    public void testTicketNoArgsConstructor() {
        // Create a ticket using the no-args constructor
        Ticket ticket = new Ticket();

        // Verify all fields are null
        assertNull(ticket.getTicketId());
        assertNull(ticket.getSubject());
        assertNull(ticket.getDescription());
        assertNull(ticket.getStatus());
        assertNull(ticket.getUserId());
        assertNull(ticket.getAssigneeId());
        assertNull(ticket.getCreatedAt());
        assertNull(ticket.getUpdatedAt());
    }

    @Test
    public void testTicketAllArgsConstructor() {
        // Create test data
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-001";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Create a ticket using the all-args constructor
        Ticket ticket = new Ticket(ticketId, subject, description, status, userId, assigneeId, createdAt, updatedAt);

        // Verify all fields are set correctly
        assertEquals(ticketId, ticket.getTicketId());
        assertEquals(subject, ticket.getSubject());
        assertEquals(description, ticket.getDescription());
        assertEquals(status, ticket.getStatus());
        assertEquals(userId, ticket.getUserId());
        assertEquals(assigneeId, ticket.getAssigneeId());
        assertEquals(createdAt, ticket.getCreatedAt());
        assertEquals(updatedAt, ticket.getUpdatedAt());
    }

    @Test
    public void testTicketSettersAndGetters() {
        // Create a ticket using the no-args constructor
        Ticket ticket = new Ticket();

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
        ticket.setTicketId(ticketId);
        ticket.setSubject(subject);
        ticket.setDescription(description);
        ticket.setStatus(status);
        ticket.setUserId(userId);
        ticket.setAssigneeId(assigneeId);
        ticket.setCreatedAt(createdAt);
        ticket.setUpdatedAt(updatedAt);

        // Verify all fields are set correctly using getters
        assertEquals(ticketId, ticket.getTicketId());
        assertEquals(subject, ticket.getSubject());
        assertEquals(description, ticket.getDescription());
        assertEquals(status, ticket.getStatus());
        assertEquals(userId, ticket.getUserId());
        assertEquals(assigneeId, ticket.getAssigneeId());
        assertEquals(createdAt, ticket.getCreatedAt());
        assertEquals(updatedAt, ticket.getUpdatedAt());
    }

    @Test
    public void testTicketStatusEnum() {
        // Verify all enum values
        assertEquals(4, Ticket.TicketStatus.values().length);
        assertEquals(Ticket.TicketStatus.OPEN, Ticket.TicketStatus.valueOf("OPEN"));
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, Ticket.TicketStatus.valueOf("IN_PROGRESS"));
        assertEquals(Ticket.TicketStatus.RESOLVED, Ticket.TicketStatus.valueOf("RESOLVED"));
        assertEquals(Ticket.TicketStatus.CLOSED, Ticket.TicketStatus.valueOf("CLOSED"));
    }
}