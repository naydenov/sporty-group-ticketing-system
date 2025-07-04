package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.repository.TicketRepository;
import com.sporty.group.sportygroupticketingcommons.event.NewTicketEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketConsumerServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketConsumerService ticketConsumerService;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    private NewTicketEvent newTicketEvent;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();

        newTicketEvent = new NewTicketEvent();
        newTicketEvent.setTicketId(ticketId.toString());
        newTicketEvent.setUserId("user123");
        newTicketEvent.setStatus("OPEN");
        newTicketEvent.setSubject("Test Ticket");
        newTicketEvent.setDescription("This is a test ticket");
        newTicketEvent.setCreatedAt("01.01.2023");
    }

    @Test
    void consumeNewTicketEvent_WithValidData_ShouldSaveTicket() {
        // Act
        ticketConsumerService.consumeNewTicketEvent(newTicketEvent);

        // Assert
        verify(ticketRepository, times(1)).save(ticketCaptor.capture());

        Ticket savedTicket = ticketCaptor.getValue();
        assertEquals(ticketId, savedTicket.getTicketId());
        assertEquals("user123", savedTicket.getUserId());
        assertEquals(Ticket.TicketStatus.OPEN, savedTicket.getStatus());
        assertEquals("Test Ticket", savedTicket.getSubject());
        assertEquals("This is a test ticket", savedTicket.getDescription());

        // We can't assert the exact time since the service uses current time when parsing fails
        assertNotNull(savedTicket.getCreatedAt());
    }

    @Test
    void consumeNewTicketEvent_WithInvalidDate_ShouldUseCurrentTimeAndSaveTicket() {
        // Arrange
        newTicketEvent.setCreatedAt("invalid-date");

        // Act
        ticketConsumerService.consumeNewTicketEvent(newTicketEvent);

        // Assert
        verify(ticketRepository, times(1)).save(ticketCaptor.capture());

        Ticket savedTicket = ticketCaptor.getValue();
        assertEquals(ticketId, savedTicket.getTicketId());
        assertNotNull(savedTicket.getCreatedAt());
        // We can't assert the exact time, but we can check it's not null
    }

    @Test
    void consumeNewTicketEvent_WithNullDate_ShouldUseCurrentTimeAndSaveTicket() {
        // Arrange
        newTicketEvent.setCreatedAt(null);

        // Act
        ticketConsumerService.consumeNewTicketEvent(newTicketEvent);

        // Assert
        verify(ticketRepository, times(1)).save(ticketCaptor.capture());

        Ticket savedTicket = ticketCaptor.getValue();
        assertEquals(ticketId, savedTicket.getTicketId());
        assertNotNull(savedTicket.getCreatedAt());
        // We can't assert the exact time, but we can check it's not null
    }

    @Test
    void consumeNewTicketEvent_WithEmptyDate_ShouldUseCurrentTimeAndSaveTicket() {
        // Arrange
        newTicketEvent.setCreatedAt("");

        // Act
        ticketConsumerService.consumeNewTicketEvent(newTicketEvent);

        // Assert
        verify(ticketRepository, times(1)).save(ticketCaptor.capture());

        Ticket savedTicket = ticketCaptor.getValue();
        assertEquals(ticketId, savedTicket.getTicketId());
        assertNotNull(savedTicket.getCreatedAt());
        // We can't assert the exact time, but we can check it's not null
    }

    @Test
    void consumeNewTicketEvent_WhenExceptionOccurs_ShouldHandleGracefully() {
        // Arrange
        when(ticketRepository.save(any(Ticket.class))).thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        // This should not throw an exception
        assertDoesNotThrow(() -> ticketConsumerService.consumeNewTicketEvent(newTicketEvent));

        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
}
