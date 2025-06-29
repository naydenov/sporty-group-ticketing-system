package com.sporty.group.ticketmanagementservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void testTicketBuilder() {
        // Given
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-007";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
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

        // Then
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
    void testTicketEqualsAndHashCode() {
        // Given
        UUID ticketId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket1 = Ticket.builder()
                .ticketId(ticketId)
                .subject("Test Subject")
                .description("Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .assigneeId("agent-007")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Ticket ticket2 = Ticket.builder()
                .ticketId(ticketId)
                .subject("Test Subject")
                .description("Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .assigneeId("agent-007")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Ticket ticket3 = Ticket.builder()
                .ticketId(UUID.randomUUID())
                .subject("Different Subject")
                .description("Different Description")
                .status(Ticket.TicketStatus.IN_PROGRESS)
                .userId("user-002")
                .assigneeId("agent-008")
                .createdAt(now.plusDays(1))
                .updatedAt(now.plusDays(1))
                .build();

        // Then
        assertEquals(ticket1, ticket2);
        assertEquals(ticket1.hashCode(), ticket2.hashCode());
        assertNotEquals(ticket1, ticket3);
        assertNotEquals(ticket1.hashCode(), ticket3.hashCode());
    }

    @Test
    void testTicketToString() {
        // Given
        UUID ticketId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Test Subject")
                .description("Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .assigneeId("agent-007")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        String ticketString = ticket.toString();

        // Then
        assertTrue(ticketString.contains(ticketId.toString()));
        assertTrue(ticketString.contains("Test Subject"));
        assertTrue(ticketString.contains("Test Description"));
        assertTrue(ticketString.contains("OPEN"));
        assertTrue(ticketString.contains("user-001"));
        assertTrue(ticketString.contains("agent-007"));
    }

    @Test
    void testTicketNoArgsConstructor() {
        // When
        Ticket ticket = new Ticket();

        // Then
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
    void testTicketAllArgsConstructor() {
        // Given
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-007";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        Ticket ticket = new Ticket(ticketId, subject, description, status, userId, assigneeId, createdAt, updatedAt);

        // Then
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
    void testTicketSetters() {
        // Given
        Ticket ticket = new Ticket();
        UUID ticketId = UUID.randomUUID();
        String subject = "Test Subject";
        String description = "Test Description";
        Ticket.TicketStatus status = Ticket.TicketStatus.OPEN;
        String userId = "user-001";
        String assigneeId = "agent-007";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        ticket.setTicketId(ticketId);
        ticket.setSubject(subject);
        ticket.setDescription(description);
        ticket.setStatus(status);
        ticket.setUserId(userId);
        ticket.setAssigneeId(assigneeId);
        ticket.setCreatedAt(createdAt);
        ticket.setUpdatedAt(updatedAt);

        // Then
        assertEquals(ticketId, ticket.getTicketId());
        assertEquals(subject, ticket.getSubject());
        assertEquals(description, ticket.getDescription());
        assertEquals(status, ticket.getStatus());
        assertEquals(userId, ticket.getUserId());
        assertEquals(assigneeId, ticket.getAssigneeId());
        assertEquals(createdAt, ticket.getCreatedAt());
        assertEquals(updatedAt, ticket.getUpdatedAt());
    }
}