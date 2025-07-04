package com.sporty.group.ticketmanagementservice.model.event;

import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
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
        TicketCreatedEvent event = TicketCreatedEvent.builder().userId(userId).subject(subject).description(description).build();

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
        TicketCreatedEvent event1 = TicketCreatedEvent.builder().userId("user-001").subject("Test Subject").description("Test Description").build();
        TicketCreatedEvent event2 = TicketCreatedEvent.builder().userId("user-001").subject("Test Subject").description("Test Description").build();
        TicketCreatedEvent event3 = TicketCreatedEvent.builder().userId("user-002").subject("Different Subject").description("Different Description").build();

        // Then
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1.hashCode(), event3.hashCode());
    }

    @Test
    void testTicketCreatedEventToString() {
        // Given
        TicketCreatedEvent event = TicketCreatedEvent.builder().userId("user-001").subject("Test Subject").description("Test Description").build();

        // When
        String eventString = event.toString();

        // Then
        assertTrue(eventString.contains("user-001"));
        assertTrue(eventString.contains("Test Subject"));
        assertTrue(eventString.contains("Test Description"));
    }
}