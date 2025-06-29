package com.sporty.group.agentassignmentservice.controller;

import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private MockMvc mockMvc;
    private List<Ticket> tickets;
    private List<Ticket> newTickets;
    private UUID ticketId;
    private UUID agentId;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();

        ticketId = UUID.randomUUID();
        agentId = UUID.randomUUID();

        // Create test tickets
        ticket = new Ticket();
        ticket.setTicketId(ticketId);
        ticket.setSubject("Test Ticket");
        ticket.setDescription("This is a test ticket");
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        ticket.setUserId("user123");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

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
        newTickets = Collections.singletonList(ticket);
    }

    @Test
    void getAllTickets_ShouldReturnAllTickets() throws Exception {
        // Arrange
        when(ticketService.getAllTickets()).thenReturn(tickets);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].subject", is("Test Ticket")))
                .andExpect(jsonPath("$[0].status", is("OPEN")))
                .andExpect(jsonPath("$[1].subject", is("Another Ticket")))
                .andExpect(jsonPath("$[1].status", is("IN_PROGRESS")));

        verify(ticketService, times(1)).getAllTickets();
    }

    @Test
    void getNewTickets_ShouldReturnOnlyNewTickets() throws Exception {
        // Arrange
        when(ticketService.getNewTickets()).thenReturn(newTickets);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tickets/new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].subject", is("Test Ticket")))
                .andExpect(jsonPath("$[0].status", is("OPEN")));

        verify(ticketService, times(1)).getNewTickets();
    }

    @Test
    void assignAgentToTicket_WhenSuccessful_ShouldReturnUpdatedTicket() throws Exception {
        // Arrange
        Ticket updatedTicket = new Ticket();
        updatedTicket.setTicketId(ticketId);
        updatedTicket.setSubject("Test Ticket");
        updatedTicket.setDescription("This is a test ticket");
        updatedTicket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        updatedTicket.setUserId("user123");
        updatedTicket.setAssigneeId(agentId);
        updatedTicket.setCreatedAt(ticket.getCreatedAt());
        updatedTicket.setUpdatedAt(LocalDateTime.now());

        when(ticketService.assignAgentToTicket(ticketId, agentId)).thenReturn(updatedTicket);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tickets/{ticketId}/assign/{agentId}", ticketId, agentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId", is(ticketId.toString())))
                .andExpect(jsonPath("$.subject", is("Test Ticket")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.assigneeId", is(agentId.toString())));

        verify(ticketService, times(1)).assignAgentToTicket(ticketId, agentId);
    }

    @Test
    void assignAgentToTicket_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(ticketService.assignAgentToTicket(ticketId, agentId))
                .thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/tickets/{ticketId}/assign/{agentId}", ticketId, agentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(ticketService, times(1)).assignAgentToTicket(ticketId, agentId);
    }

    @Test
    void getAllTickets_WhenNoTickets_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(ticketService.getAllTickets()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(ticketService, times(1)).getAllTickets();
    }

    @Test
    void getNewTickets_WhenNoNewTickets_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(ticketService.getNewTickets()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/tickets/new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(ticketService, times(1)).getNewTickets();
    }
}