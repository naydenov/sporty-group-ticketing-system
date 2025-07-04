package com.sporty.group.ticketmanagementservice.model.event;

import com.sporty.group.sportygroupticketingcommons.event.TicketAssignedEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketAssignedEventTest {

    @Test
    void testTicketAssignedEventConstructorAndGetters() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        String assigneeId = "agent-007";

        // When
        TicketAssignedEvent event = new TicketAssignedEvent(ticketId, assigneeId);

        // Then
        assertEquals(ticketId, event.getTicketId());
        assertEquals(assigneeId, event.getAssigneeId());
    }

    @Test
    void testTicketAssignedEventNoArgsConstructor() {
        // When
        TicketAssignedEvent event = new TicketAssignedEvent();

        // Then
        assertNull(event.getTicketId());
        assertNull(event.getAssigneeId());
    }

    @Test
    void testTicketAssignedEventSetters() {
        // Given
        TicketAssignedEvent event = new TicketAssignedEvent();
        String ticketId = UUID.randomUUID().toString();
        String assigneeId = "agent-007";

        // When
        event.setTicketId(ticketId);
        event.setAssigneeId(assigneeId);

        // Then
        assertEquals(ticketId, event.getTicketId());
        assertEquals(assigneeId, event.getAssigneeId());
    }

    @Test
    void testTicketAssignedEventEqualsAndHashCode() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        TicketAssignedEvent event1 = new TicketAssignedEvent(ticketId, "agent-007");
        TicketAssignedEvent event2 = new TicketAssignedEvent(ticketId, "agent-007");
        TicketAssignedEvent event3 = new TicketAssignedEvent(UUID.randomUUID().toString(), "agent-008");

        // Then
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1.hashCode(), event3.hashCode());
    }

    @Test
    void testTicketAssignedEventToString() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        String assigneeId = "agent-007";
        TicketAssignedEvent event = new TicketAssignedEvent(ticketId, assigneeId);

        // When
        String eventString = event.toString();

        // Then
        assertTrue(eventString.contains(ticketId));
        assertTrue(eventString.contains(assigneeId));
    }
}