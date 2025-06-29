package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AgentService agentService;

    @Mock
    private TicketProducerService ticketProducerService;

    @InjectMocks
    private TicketService ticketService;

    private UUID ticketId;
    private UUID agentId;
    private Ticket ticket;
    private Agent agent;
    private List<Ticket> tickets;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        agentId = UUID.randomUUID();

        // Set up ticket
        ticket = new Ticket();
        ticket.setTicketId(ticketId);
        ticket.setSubject("Test Ticket");
        ticket.setDescription("This is a test ticket");
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        ticket.setUserId("user123");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        // Set up another ticket
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId(UUID.randomUUID());
        ticket2.setSubject("Another Ticket");
        ticket2.setDescription("This is another test ticket");
        ticket2.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        ticket2.setUserId("user456");
        ticket2.setAssigneeId(UUID.randomUUID());
        ticket2.setCreatedAt(LocalDateTime.now().minusDays(1));
        ticket2.setUpdatedAt(LocalDateTime.now().minusDays(1));

        tickets = Arrays.asList(ticket, ticket2);

        // Set up agent
        agent = new Agent();
        agent.setAgentId(agentId);
        agent.setFirstName("John");
        agent.setLastName("Doe");
        agent.setAvailability(Agent.AgentAvailability.AVAILABLE);
        agent.setSkills(new HashSet<>(Arrays.asList("Java", "Spring")));
    }

    @Test
    void getAllTickets_ShouldReturnAllTickets() {
        // Arrange
        when(ticketRepository.findAll()).thenReturn(tickets);

        // Act
        List<Ticket> result = ticketService.getAllTickets();

        // Assert
        assertEquals(2, result.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void getNewTickets_ShouldReturnOnlyOpenTickets() {
        // Arrange
        when(ticketRepository.findByStatus(Ticket.TicketStatus.OPEN))
                .thenReturn(Collections.singletonList(ticket));

        // Act
        List<Ticket> result = ticketService.getNewTickets();

        // Assert
        assertEquals(1, result.size());
        assertEquals(Ticket.TicketStatus.OPEN, result.get(0).getStatus());
        verify(ticketRepository, times(1)).findByStatus(Ticket.TicketStatus.OPEN);
    }

    @Test
    void getTicketById_WhenTicketExists_ShouldReturnTicket() {
        // Arrange
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act
        Optional<Ticket> result = ticketService.getTicketById(ticketId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ticketId, result.get().getTicketId());
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void getTicketById_WhenTicketDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(ticketRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Ticket> result = ticketService.getTicketById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
        verify(ticketRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void assignAgentToTicket_WhenTicketAndAgentExistAndAreValid_ShouldAssignAndReturnUpdatedTicket() {
        // Arrange
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(agentService.getAgentById(agentId)).thenReturn(Optional.of(agent));
        
        Ticket updatedTicket = new Ticket();
        updatedTicket.setTicketId(ticketId);
        updatedTicket.setSubject("Test Ticket");
        updatedTicket.setDescription("This is a test ticket");
        updatedTicket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        updatedTicket.setUserId("user123");
        updatedTicket.setAssigneeId(agentId);
        updatedTicket.setCreatedAt(ticket.getCreatedAt());
        updatedTicket.setUpdatedAt(LocalDateTime.now());
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(updatedTicket);
        
        // Act
        Ticket result = ticketService.assignAgentToTicket(ticketId, agentId);

        // Assert
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, result.getStatus());
        assertEquals(agentId, result.getAssigneeId());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(agentService, times(1)).getAgentById(agentId);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(agentService, times(1)).updateAgentAvailability(agentId, Agent.AgentAvailability.NOT_AVAILABLE, ticketId);
        verify(ticketProducerService, times(1)).sendTicketAssignedEvent(ticketId, agentId);
    }

    @Test
    void assignAgentToTicket_WhenTicketDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(ticketRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            ticketService.assignAgentToTicket(nonExistentId, agentId)
        );
        
        assertEquals("Ticket not found with ID: " + nonExistentId, exception.getMessage());
        verify(ticketRepository, times(1)).findById(nonExistentId);
        verify(agentService, never()).getAgentById(any(UUID.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(agentService, never()).updateAgentAvailability(any(UUID.class), any(Agent.AgentAvailability.class), any(UUID.class));
        verify(ticketProducerService, never()).sendTicketAssignedEvent(any(UUID.class), any(UUID.class));
    }

    @Test
    void assignAgentToTicket_WhenTicketIsAlreadyAssigned_ShouldThrowException() {
        // Arrange
        ticket.setAssigneeId(UUID.randomUUID());
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            ticketService.assignAgentToTicket(ticketId, agentId)
        );
        
        assertEquals("Ticket is already assigned to an agent", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(agentService, never()).getAgentById(any(UUID.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(agentService, never()).updateAgentAvailability(any(UUID.class), any(Agent.AgentAvailability.class), any(UUID.class));
        verify(ticketProducerService, never()).sendTicketAssignedEvent(any(UUID.class), any(UUID.class));
    }

    @Test
    void assignAgentToTicket_WhenAgentDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentAgentId = UUID.randomUUID();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(agentService.getAgentById(nonExistentAgentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            ticketService.assignAgentToTicket(ticketId, nonExistentAgentId)
        );
        
        assertEquals("Agent not found with ID: " + nonExistentAgentId, exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(agentService, times(1)).getAgentById(nonExistentAgentId);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(agentService, never()).updateAgentAvailability(any(UUID.class), any(Agent.AgentAvailability.class), any(UUID.class));
        verify(ticketProducerService, never()).sendTicketAssignedEvent(any(UUID.class), any(UUID.class));
    }

    @Test
    void assignAgentToTicket_WhenAgentIsNotAvailable_ShouldThrowException() {
        // Arrange
        agent.setAvailability(Agent.AgentAvailability.NOT_AVAILABLE);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(agentService.getAgentById(agentId)).thenReturn(Optional.of(agent));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            ticketService.assignAgentToTicket(ticketId, agentId)
        );
        
        assertEquals("Agent is not available", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(agentService, times(1)).getAgentById(agentId);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(agentService, never()).updateAgentAvailability(any(UUID.class), any(Agent.AgentAvailability.class), any(UUID.class));
        verify(ticketProducerService, never()).sendTicketAssignedEvent(any(UUID.class), any(UUID.class));
    }
}