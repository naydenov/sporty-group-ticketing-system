package com.sporty.group.ticketmanagementservice.model.event;

import com.sporty.group.sportygroupticketingcommons.event.NewTicketEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NewTicketEventTest {

    @Test
    void testNewTicketEventBuilder() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        String status = "open";
        String subject = "Test Subject";
        String description = "Test Description";
        String createdAt = "15.03.2025";

        // When
        NewTicketEvent event = NewTicketEvent.builder()
                .ticketId(ticketId)
                .status(status)
                .subject(subject)
                .description(description)
                .createdAt(createdAt)
                .build();

        // Then
        assertEquals(ticketId, event.getTicketId());
        assertEquals(status, event.getStatus());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
        assertEquals(createdAt, event.getCreatedAt());
    }

    @Test
    void testNewTicketEventNoArgsConstructor() {
        // When
        NewTicketEvent event = new NewTicketEvent();

        // Then
        assertNull(event.getTicketId());
        assertNull(event.getStatus());
        assertNull(event.getSubject());
        assertNull(event.getDescription());
        assertNull(event.getCreatedAt());
    }

    @Test
    void testNewTicketEventAllArgsConstructor() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        String status = "open";
        String subject = "Test Subject";
        String description = "Test Description";
        String createdAt = "15.03.2025";

        // When
        NewTicketEvent event = NewTicketEvent.builder().ticketId(ticketId).status(status).subject(subject).description(description).createdAt(createdAt).build();

        // Then
        assertEquals(ticketId, event.getTicketId());
        assertEquals(status, event.getStatus());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
        assertEquals(createdAt, event.getCreatedAt());
    }

    @Test
    void testNewTicketEventSetters() {
        // Given
        NewTicketEvent event = new NewTicketEvent();
        String ticketId = UUID.randomUUID().toString();
        String status = "open";
        String subject = "Test Subject";
        String description = "Test Description";
        String createdAt = "15.03.2025";

        // When
        event.setTicketId(ticketId);
        event.setStatus(status);
        event.setSubject(subject);
        event.setDescription(description);
        event.setCreatedAt(createdAt);

        // Then
        assertEquals(ticketId, event.getTicketId());
        assertEquals(status, event.getStatus());
        assertEquals(subject, event.getSubject());
        assertEquals(description, event.getDescription());
        assertEquals(createdAt, event.getCreatedAt());
    }

    @Test
    void testNewTicketEventEqualsAndHashCode() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        NewTicketEvent event1 = NewTicketEvent.builder()
                .ticketId(ticketId)
                .status("open")
                .subject("Test Subject")
                .description("Test Description")
                .createdAt("15.03.2025")
                .build();

        NewTicketEvent event2 = NewTicketEvent.builder()
                .ticketId(ticketId)
                .status("open")
                .subject("Test Subject")
                .description("Test Description")
                .createdAt("15.03.2025")
                .build();

        NewTicketEvent event3 = NewTicketEvent.builder()
                .ticketId(UUID.randomUUID().toString())
                .status("in_progress")
                .subject("Different Subject")
                .description("Different Description")
                .createdAt("16.03.2025")
                .build();

        // Then
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1.hashCode(), event3.hashCode());
    }

    @Test
    void testNewTicketEventToString() {
        // Given
        String ticketId = UUID.randomUUID().toString();
        String status = "open";
        String subject = "Test Subject";
        String description = "Test Description";
        String createdAt = "15.03.2025";

        NewTicketEvent event = NewTicketEvent.builder()
                .ticketId(ticketId)
                .status(status)
                .subject(subject)
                .description(description)
                .createdAt(createdAt)
                .build();

        // When
        String eventString = event.toString();

        // Then
        assertTrue(eventString.contains(ticketId));
        assertTrue(eventString.contains(status));
        assertTrue(eventString.contains(subject));
        assertTrue(eventString.contains(description));
        assertTrue(eventString.contains(createdAt));
    }
}