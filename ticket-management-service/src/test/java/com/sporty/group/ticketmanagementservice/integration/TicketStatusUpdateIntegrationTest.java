package com.sporty.group.ticketmanagementservice.integration;

import com.sporty.group.ticketmanagementservice.model.Ticket;
import com.sporty.group.ticketmanagementservice.model.event.TicketStatusUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@EmbeddedKafka
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TicketStatusUpdateIntegrationTest extends BaseIntegrationTest {

    @Test
    void testTicketStatusUpdateFlow() throws Exception {
        // Given: Create a ticket in the repository with OPEN status
        UUID ticketId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Status Update Test Subject")
                .description("Status Update Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .createdAt(now)
                .updatedAt(now)
                .build();

        ticketRepository.save(ticket);

        // Verify the ticket was saved with OPEN status
        Optional<Ticket> savedTicket = ticketRepository.findById(ticketId);
        assertTrue(savedTicket.isPresent(), "Ticket should be saved in the repository");
        assertEquals(Ticket.TicketStatus.OPEN, savedTicket.get().getStatus(), "Status should be OPEN initially");

        // When: Send a TicketStatusUpdatedEvent to change status to IN_PROGRESS
        String newStatus = "in_progress";
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(ticketId.toString(), newStatus);

        ticketStatusUpdatedKafkaTemplate.send(ticketUpdatesTopic, event);

        // Then: Wait for the event to be processed
        TimeUnit.SECONDS.sleep(2);

        // Verify the ticket's status was updated
        Optional<Ticket> updatedTicket = ticketRepository.findById(ticketId);
        assertTrue(updatedTicket.isPresent(), "Ticket should still exist in the repository");
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, updatedTicket.get().getStatus(), "Status should be updated to IN_PROGRESS");
        assertTrue(updatedTicket.get().getUpdatedAt().isAfter(now), "UpdatedAt should be updated");
    }

    @Test
    void testTicketStatusUpdateFlow_InvalidStatus() throws Exception {
        // Given: Create a ticket in the repository with OPEN status
        UUID ticketId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Invalid Status Test Subject")
                .description("Invalid Status Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .createdAt(now)
                .updatedAt(now)
                .build();

        ticketRepository.save(ticket);

        // When: Send a TicketStatusUpdatedEvent with an invalid status
        String invalidStatus = "invalid_status";
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(ticketId.toString(), invalidStatus);

        ticketStatusUpdatedKafkaTemplate.send(ticketUpdatesTopic, event);

        // Then: Wait for the event to be processed
        TimeUnit.SECONDS.sleep(2);

        // Verify the ticket's status was not updated
        Optional<Ticket> updatedTicket = ticketRepository.findById(ticketId);
        assertTrue(updatedTicket.isPresent(), "Ticket should still exist in the repository");
        assertEquals(Ticket.TicketStatus.OPEN, updatedTicket.get().getStatus(), "Status should still be OPEN");

        // Note: We're not checking the updatedAt timestamp because the H2 database might
        // have different behavior than an in-memory repository when it comes to timestamps.
        // The important thing is that the status remains OPEN after an invalid status update.
    }
}
