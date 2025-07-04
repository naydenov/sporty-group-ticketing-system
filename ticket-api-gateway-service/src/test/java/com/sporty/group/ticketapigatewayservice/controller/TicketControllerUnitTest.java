package com.sporty.group.ticketapigatewayservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.group.ticketapigatewayservice.dto.TicketRequest;
import com.sporty.group.sportygroupticketingcommons.model.Ticket;
import com.sporty.group.ticketapigatewayservice.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    public void testCreateTicket() throws Exception {
        // Create a ticket request
        TicketRequest ticketRequest = TicketRequest.builder()
                .userId("user-001")
                .subject("Login problem")
                .description("Cannot reset my password")
                .build();

        // Create a mock ticket response
        UUID ticketId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Ticket mockTicket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Login problem")
                .description("Cannot reset my password")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .assigneeId(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Mock the service method
        when(ticketService.createTicket(any(TicketRequest.class))).thenReturn(mockTicket);

        // Perform POST request and verify response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ticketId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subject").value("Login problem"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Cannot reset my password"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OPEN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());
    }
}
