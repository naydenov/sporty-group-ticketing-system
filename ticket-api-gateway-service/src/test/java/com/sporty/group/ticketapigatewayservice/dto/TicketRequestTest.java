package com.sporty.group.ticketapigatewayservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TicketRequestTest {

    @Test
    public void testTicketRequestBuilder() {
        // Create test data
        String userId = "user-001";
        String subject = "Test Subject";
        String description = "Test Description";

        // Build a ticket request using the builder
        TicketRequest request = TicketRequest.builder()
                .userId(userId)
                .subject(subject)
                .description(description)
                .build();

        // Verify all fields are set correctly
        assertEquals(userId, request.getUserId());
        assertEquals(subject, request.getSubject());
        assertEquals(description, request.getDescription());
    }

    @Test
    public void testTicketRequestNoArgsConstructor() {
        // Create a ticket request using the no-args constructor
        TicketRequest request = new TicketRequest();

        // Verify all fields are null
        assertNull(request.getUserId());
        assertNull(request.getSubject());
        assertNull(request.getDescription());
    }

    @Test
    public void testTicketRequestAllArgsConstructor() {
        // Create test data
        String userId = "user-001";
        String subject = "Test Subject";
        String description = "Test Description";

        // Create a ticket request using the all-args constructor
        TicketRequest request = new TicketRequest(userId, subject, description);

        // Verify all fields are set correctly
        assertEquals(userId, request.getUserId());
        assertEquals(subject, request.getSubject());
        assertEquals(description, request.getDescription());
    }

    @Test
    public void testTicketRequestSettersAndGetters() {
        // Create a ticket request using the no-args constructor
        TicketRequest request = new TicketRequest();

        // Create test data
        String userId = "user-001";
        String subject = "Test Subject";
        String description = "Test Description";

        // Set the fields using setters
        request.setUserId(userId);
        request.setSubject(subject);
        request.setDescription(description);

        // Verify all fields are set correctly using getters
        assertEquals(userId, request.getUserId());
        assertEquals(subject, request.getSubject());
        assertEquals(description, request.getDescription());
    }

    @Test
    public void testTicketRequestEqualsAndHashCode() {
        // Create two identical ticket requests
        TicketRequest request1 = new TicketRequest("user-001", "Test Subject", "Test Description");
        TicketRequest request2 = new TicketRequest("user-001", "Test Subject", "Test Description");

        // Verify they are equal and have the same hash code
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        // Create a different ticket request
        TicketRequest request3 = new TicketRequest("user-002", "Test Subject", "Test Description");

        // Verify they are not equal
        assertNotEquals(request1, request3);
    }

    @Test
    public void testTicketRequestToString() {
        // Create a ticket request
        TicketRequest request = new TicketRequest("user-001", "Test Subject", "Test Description");

        // Verify the toString method includes all fields
        String toString = request.toString();
        assertTrue(toString.contains("userId=user-001"));
        assertTrue(toString.contains("subject=Test Subject"));
        assertTrue(toString.contains("description=Test Description"));
    }
}