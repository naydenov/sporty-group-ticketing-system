package com.sporty.group.ticketmanagementservice.model.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketStatusUpdatedEventTest {

    @Test
    void testTicketStatusUpdatedEventConstructorAndGetters() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        String status = "in_progress";

        // When
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(ticketId, status);

        // Then
        assertEquals(ticketId, event.getTicketId());
        assertEquals(status, event.getStatus());
    }

    @Test
    void testTicketStatusUpdatedEventNoArgsConstructor() {
        // When
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent();

        // Then
        assertNull(event.getTicketId());
        assertNull(event.getStatus());
    }

    @Test
    void testTicketStatusUpdatedEventSetters() {
        // Given
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent();
        String ticketId = UUID.randomUUID().toString();
        String status = "in_progress";

        // When
        event.setTicketId(ticketId);
        event.setStatus(status);

        // Then
        assertEquals(ticketId, event.getTicketId());
        assertEquals(status, event.getStatus());
    }

    @Test
    void testTicketStatusUpdatedEventEqualsAndHashCode() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        TicketStatusUpdatedEvent event1 = new TicketStatusUpdatedEvent(ticketId, "in_progress");
        TicketStatusUpdatedEvent event2 = new TicketStatusUpdatedEvent(ticketId, "in_progress");
        TicketStatusUpdatedEvent event3 = new TicketStatusUpdatedEvent(UUID.randomUUID().toString(), "resolved");

        // Then
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1.hashCode(), event3.hashCode());
    }

    @Test
    void testTicketStatusUpdatedEventToString() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        String status = "in_progress";
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(ticketId, status);

        // When
        String eventString = event.toString();

        // Then
        assertTrue(eventString.contains(ticketId));
        assertTrue(eventString.contains(status));
    }
}