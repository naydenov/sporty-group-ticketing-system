package com.sporty.group.ticketmanagementservice.listener;

import com.sporty.group.sportygroupticketingcommons.event.TicketAssignedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketStatusUpdatedEvent;
import com.sporty.group.ticketmanagementservice.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaEventListenerTest {

    @Mock
    private TicketService ticketService;

    private KafkaEventListener kafkaEventListener;

    @BeforeEach
    void setUp() {
        kafkaEventListener = new KafkaEventListener(ticketService);
    }

    @Test
    void listenTicketCreated_shouldDelegateToTicketService() {
        // Given
        TicketCreatedEvent event =  TicketCreatedEvent.builder().userId("user-001").subject("Test Subject").description("Test Description").build();

        // When
        kafkaEventListener.listenTicketCreated(event);

        // Then
        verify(ticketService).processTicketCreated(event);
    }

    @Test
    void listenTicketAssigned_shouldDelegateToTicketService() {
        // Given
        TicketAssignedEvent event = new TicketAssignedEvent(UUID.randomUUID().toString(), "agent-007");

        // When
        kafkaEventListener.listenTicketAssigned(event);

        // Then
        verify(ticketService).processTicketAssigned(event);
    }

    @Test
    void listenTicketStatusUpdated_shouldDelegateToTicketService() {
        // Given
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(UUID.randomUUID().toString(), "in_progress");

        // When
        kafkaEventListener.listenTicketStatusUpdated(event);

        // Then
        verify(ticketService).processTicketStatusUpdated(event);
    }
}