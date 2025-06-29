package com.sporty.group.ticketmanagementservice.model.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketCreatedEventTest {

    @Test
    void testTicketCreatedEventConstructorAndGetters() {
        // Given
        String userId = "user-001";
        String subject = "Test Subject";
        String description = "Test Description";

        // When
        TicketCreatedEvent event = new TicketCreatedEvent(userId, subject, description);

        // Then
        assertEquals(userId, event.getUserId());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
    }

    @Test
    void testTicketCreatedEventNoArgsConstructor() {
        // When
        TicketCreatedEvent event = new TicketCreatedEvent();

        // Then
        assertNull(event.getUserId());
        assertNull(event.getSubject());
        assertNull(event.getDescription());
    }

    @Test
    void testTicketCreatedEventSetters() {
        // Given
        TicketCreatedEvent event = new TicketCreatedEvent();
        String userId = "user-001";
        String subject = "Test Subject";
        String description = "Test Description";

        // When
        event.setUserId(userId);
        event.setSubject(subject);
        event.setDescription(description);

        // Then
        assertEquals(userId, event.getUserId());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
    }

    @Test
    void testTicketCreatedEventEqualsAndHashCode() {
        // Given
        TicketCreatedEvent event1 = new TicketCreatedEvent("user-001", "Test Subject", "Test Description");
        TicketCreatedEvent event2 = new TicketCreatedEvent("user-001", "Test Subject", "Test Description");
        TicketCreatedEvent event3 = new TicketCreatedEvent("user-002", "Different Subject", "Different Description");

        // Then
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1.hashCode(), event3.hashCode());
    }

    @Test
    void testTicketCreatedEventToString() {
        // Given
        TicketCreatedEvent event = new TicketCreatedEvent("user-001", "Test Subject", "Test Description");

        // When
        String eventString = event.toString();

        // Then
        assertTrue(eventString.contains("user-001"));
        assertTrue(eventString.contains("Test Subject"));
        assertTrue(eventString.contains("Test Description"));
    }
}