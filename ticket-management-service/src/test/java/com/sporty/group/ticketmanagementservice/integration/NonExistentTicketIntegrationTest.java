package com.sporty.group.ticketmanagementservice.integration;

import com.sporty.group.sportygroupticketingcommons.event.TicketAssignedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketStatusUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@EmbeddedKafka
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NonExistentTicketIntegrationTest extends BaseIntegrationTest {

    @Test
    void testTicketAssignedEvent_WithNonExistentTicket() throws Exception {
        // Given: A non-existent ticket ID
        UUID nonExistentTicketId = UUID.randomUUID();
        String assigneeId = "agent-007";

        // When: Send a TicketAssignedEvent with the non-existent ticket ID
        TicketAssignedEvent event = new TicketAssignedEvent(nonExistentTicketId.toString(), assigneeId);

        // Then: No exception should be thrown
        assertDoesNotThrow(() -> {
            ticketAssignedKafkaTemplate.send(ticketAssignmentsTopic, event);

            // Wait for the event to be processed
            TimeUnit.SECONDS.sleep(2);
        });
    }

    @Test
    void testTicketStatusUpdatedEvent_WithNonExistentTicket() throws Exception {
        // Given: A non-existent ticket ID
        UUID nonExistentTicketId = UUID.randomUUID();
        String newStatus = "in_progress";

        // When: Send a TicketStatusUpdatedEvent with the non-existent ticket ID
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(nonExistentTicketId.toString(), newStatus);

        // Then: No exception should be thrown
        assertDoesNotThrow(() -> {
            ticketStatusUpdatedKafkaTemplate.send(ticketUpdatesTopic, event);

            // Wait for the event to be processed
            TimeUnit.SECONDS.sleep(2);
        });
    }

    @Test
    void testMultipleEvents_WithNonExistentTicket() throws Exception {
        // Given: A non-existent ticket ID
        UUID nonExistentTicketId = UUID.randomUUID();

        // When: Send multiple events with the non-existent ticket ID
        TicketAssignedEvent assignEvent = new TicketAssignedEvent(nonExistentTicketId.toString(), "agent-007");
        TicketStatusUpdatedEvent statusEvent = new TicketStatusUpdatedEvent(nonExistentTicketId.toString(), "in_progress");

        // Then: No exception should be thrown
        assertDoesNotThrow(() -> {
            // Send the assign event
            ticketAssignedKafkaTemplate.send(ticketAssignmentsTopic, assignEvent);

            // Wait for the event to be processed
            TimeUnit.SECONDS.sleep(2);

            // Send the status update event
            ticketStatusUpdatedKafkaTemplate.send(ticketUpdatesTopic, statusEvent);

            // Wait for the event to be processed
            TimeUnit.SECONDS.sleep(2);
        });
    }
}
