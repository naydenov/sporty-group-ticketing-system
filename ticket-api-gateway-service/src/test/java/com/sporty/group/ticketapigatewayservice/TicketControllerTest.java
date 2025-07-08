package com.sporty.group.ticketapigatewayservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.group.ticketapigatewayservice.dto.TicketRequest;
import com.sporty.group.sportygroupticketingcommons.model.Ticket;
import com.sporty.group.sportygroupticketingcommons.model.Ticket.TicketStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = {"ticket-created"})
@DirtiesContext
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateTicket() throws Exception {
        // Create a ticket request
        TicketRequest ticketRequest = TicketRequest.builder()
                .userId("user-001")
                .subject("Login problem")
                .description("Cannot reset my password")
                .build();

        // Perform POST request and verify response
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Parse and verify the created ticket
        Ticket createdTicket = objectMapper.readValue(result.getResponse().getContentAsString(), Ticket.class);

        Assertions.assertNotNull(createdTicket.getTicketId());
        Assertions.assertEquals("Login problem", createdTicket.getSubject());
        Assertions.assertEquals("Cannot reset my password", createdTicket.getDescription());
        Assertions.assertEquals("user-001", createdTicket.getUserId());
        Assertions.assertEquals(TicketStatus.OPEN, createdTicket.getStatus());
        Assertions.assertNotNull(createdTicket.getCreatedAt());
        Assertions.assertNotNull(createdTicket.getUpdatedAt());
    }
}
