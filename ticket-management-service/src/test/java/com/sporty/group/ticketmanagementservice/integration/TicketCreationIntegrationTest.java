package com.sporty.group.ticketmanagementservice.integration;

import com.sporty.group.sportygroupticketingcommons.event.NewTicketEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import com.sporty.group.ticketmanagementservice.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@EmbeddedKafka
class TicketCreationIntegrationTest extends BaseIntegrationTest {

    @Test
    void testTicketCreationFlow() throws Exception {
        // Given
        String userId = "user-001";
        String subject = "Integration Test Subject";
        String description = "Integration Test Description";
        
        TicketCreatedEvent event = TicketCreatedEvent.builder().userId(userId).subject(subject).description(description).build();
        
        // When: Send the event to Kafka
        ticketCreatedKafkaTemplate.send(supportTicketsTopic, event);
        
        // Then: Wait for the NewTicketEvent to be published
        NewTicketEvent newTicketEvent = receiveNewTicketEvent(10);
        
        // Verify the NewTicketEvent
        assertNotNull(newTicketEvent, "NewTicketEvent should not be null");
        assertEquals(subject, newTicketEvent.getSubject(), "Subject should match");
        assertEquals(description, newTicketEvent.getDescription(), "Description should match");
        assertEquals("open", newTicketEvent.getStatus(), "Status should be 'open'");
        
        // Verify the ticket was saved in the repository
        // First, we need to get the ticketId from the NewTicketEvent
        UUID ticketId = UUID.fromString(newTicketEvent.getTicketId());
        
        // Give the system a moment to process the event and save the ticket
        TimeUnit.SECONDS.sleep(1);
        
        // Check if the ticket exists in the repository
        Optional<Ticket> savedTicket = ticketRepository.findById(ticketId);
        
        assertTrue(savedTicket.isPresent(), "Ticket should be saved in the repository");
        assertEquals(subject, savedTicket.get().getSubject(), "Subject should match");
        assertEquals(description, savedTicket.get().getDescription(), "Description should match");
        assertEquals(Ticket.TicketStatus.OPEN, savedTicket.get().getStatus(), "Status should be OPEN");
        assertEquals(userId, savedTicket.get().getUserId(), "UserId should match");
    }
}