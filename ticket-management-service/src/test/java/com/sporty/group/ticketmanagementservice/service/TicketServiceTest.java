package com.sporty.group.ticketmanagementservice.service;

import com.sporty.group.sportygroupticketingcommons.event.NewTicketEvent;
import com.sporty.group.ticketmanagementservice.model.Ticket;
import com.sporty.group.sportygroupticketingcommons.event.TicketAssignedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketStatusUpdatedEvent;
import com.sporty.group.ticketmanagementservice.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private KafkaTemplate<String, NewTicketEvent> newTicketKafkaTemplate;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    @Captor
    private ArgumentCaptor<NewTicketEvent> newTicketEventCaptor;

    private TicketService ticketService;
    private static final String TICKET_CREATED_TOPIC = "ticket-created";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(ticketRepository, newTicketKafkaTemplate, TICKET_CREATED_TOPIC);
    }

    @Test
    void processTicketCreated_shouldCreateTicketAndSendEvent() {
        // Given
        TicketCreatedEvent event = TicketCreatedEvent.builder().userId("user-001").subject("Test Subject").description("Test Description").build();

        // When
        ticketService.processTicketCreated(event);

        // Then
        verify(ticketRepository).save(ticketCaptor.capture());
        Ticket savedTicket = ticketCaptor.getValue();
        
        assertNotNull(savedTicket.getTicketId());
        assertEquals("Test Subject", savedTicket.getSubject());
        assertEquals("Test Description", savedTicket.getDescription());
        assertEquals(Ticket.TicketStatus.OPEN, savedTicket.getStatus());
        assertEquals("user-001", savedTicket.getUserId());
        assertNotNull(savedTicket.getCreatedAt());
        assertNotNull(savedTicket.getUpdatedAt());

        verify(newTicketKafkaTemplate).send(eq(TICKET_CREATED_TOPIC), newTicketEventCaptor.capture());
        NewTicketEvent sentEvent = newTicketEventCaptor.getValue();
        
        assertEquals(savedTicket.getTicketId().toString(), sentEvent.getTicketId());
        assertEquals("open", sentEvent.getStatus());
        assertEquals("Test Subject", sentEvent.getSubject());
        assertEquals("Test Description", sentEvent.getDescription());
        assertEquals(savedTicket.getCreatedAt().format(DATE_FORMATTER), sentEvent.getCreatedAt());
    }

    @Test
    void processTicketAssigned_shouldUpdateTicketAssignee_whenTicketExists() {
        // Given
        UUID ticketId = UUID.randomUUID();
        String assigneeId = "agent-007";
        TicketAssignedEvent event = new TicketAssignedEvent(ticketId.toString(), assigneeId);
        
        Ticket existingTicket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Test Subject")
                .description("Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existingTicket));

        // When
        ticketService.processTicketAssigned(event);

        // Then
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository).save(ticketCaptor.capture());
        
        Ticket updatedTicket = ticketCaptor.getValue();
        assertEquals(assigneeId, updatedTicket.getAssigneeId());
        assertNotNull(updatedTicket.getUpdatedAt());
    }

    @Test
    void processTicketAssigned_shouldDoNothing_whenTicketDoesNotExist() {
        // Given
        UUID ticketId = UUID.randomUUID();
        TicketAssignedEvent event = new TicketAssignedEvent(ticketId.toString(), "agent-007");
        
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When
        ticketService.processTicketAssigned(event);

        // Then
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void processTicketStatusUpdated_shouldUpdateTicketStatus_whenTicketExists() {
        // Given
        UUID ticketId = UUID.randomUUID();
        String newStatus = "in_progress";
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(ticketId.toString(), newStatus);
        
        Ticket existingTicket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Test Subject")
                .description("Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existingTicket));

        // When
        ticketService.processTicketStatusUpdated(event);

        // Then
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository).save(ticketCaptor.capture());
        
        Ticket updatedTicket = ticketCaptor.getValue();
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, updatedTicket.getStatus());
        assertNotNull(updatedTicket.getUpdatedAt());
    }

    @Test
    void processTicketStatusUpdated_shouldDoNothing_whenTicketDoesNotExist() {
        // Given
        UUID ticketId = UUID.randomUUID();
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(ticketId.toString(), "in_progress");
        
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When
        ticketService.processTicketStatusUpdated(event);

        // Then
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void processTicketStatusUpdated_shouldHandleInvalidStatus() {
        // Given
        UUID ticketId = UUID.randomUUID();
        String invalidStatus = "invalid_status";
        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(ticketId.toString(), invalidStatus);
        
        Ticket existingTicket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Test Subject")
                .description("Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existingTicket));

        // When
        ticketService.processTicketStatusUpdated(event);

        // Then
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository, never()).save(any());
    }
}