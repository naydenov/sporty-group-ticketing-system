package com.sporty.group.ticketapigatewayservice.service;

import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import com.sporty.group.ticketapigatewayservice.dto.TicketRequest;
import com.sporty.group.sportygroupticketingcommons.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private KafkaTemplate<String, TicketCreatedEvent> kafkaTemplate;

    @InjectMocks
    private TicketService ticketService;

    @Captor
    private ArgumentCaptor<TicketCreatedEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    private TicketRequest ticketRequest;

    @BeforeEach
    public void setup() {
        ticketRequest = TicketRequest.builder()
                .userId("user-001")
                .subject("Login problem")
                .description("Cannot reset my password")
                .build();
    }

    @Test
    public void testCreateTicket() {
        // Mock the KafkaTemplate send method
        when(kafkaTemplate.send(anyString(), anyString(), any(TicketCreatedEvent.class)))
                .thenReturn(null); // Return value doesn't matter for this test

        // Call the service method
        Ticket result = ticketService.createTicket(ticketRequest);

        // Verify the ticket was created with the correct values
        assertNotNull(result);
        assertNotNull(result.getTicketId());
        assertEquals("Login problem", result.getSubject());
        assertEquals("Cannot reset my password", result.getDescription());
        assertEquals(Ticket.TicketStatus.OPEN, result.getStatus());
        assertEquals("user-001", result.getUserId());
        assertNull(result.getAssigneeId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // Verify the Kafka message was sent
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());
        
        // Verify the topic
        assertEquals("support-tickets", topicCaptor.getValue());
        
        // Verify the key is the ticket ID as a string
        assertEquals(result.getTicketId().toString(), keyCaptor.getValue());
        
        // Verify the event data
        TicketCreatedEvent event = eventCaptor.getValue();
        assertEquals(result.getTicketId(), event.getTicketId());
        assertEquals("Login problem", event.getSubject());
        assertEquals("Cannot reset my password", event.getDescription());
        assertEquals(Ticket.TicketStatus.OPEN, event.getStatus());
        assertEquals("user-001", event.getUserId());
        assertNull(event.getAssigneeId());
        assertEquals(result.getCreatedAt(), event.getCreatedAt());
        assertEquals(result.getUpdatedAt(), event.getUpdatedAt());
    }

    @Test
    public void testCreateTicket_KafkaException() {
        // Mock the KafkaTemplate to throw an exception
        when(kafkaTemplate.send(anyString(), anyString(), any(TicketCreatedEvent.class)))
                .thenThrow(new RuntimeException("Kafka error"));

        // Call the service method - should not throw exception
        Ticket result = ticketService.createTicket(ticketRequest);

        // Verify the ticket was still created correctly despite Kafka error
        assertNotNull(result);
        assertEquals("Login problem", result.getSubject());
        assertEquals("Cannot reset my password", result.getDescription());
        assertEquals(Ticket.TicketStatus.OPEN, result.getStatus());
        assertEquals("user-001", result.getUserId());
    }
}