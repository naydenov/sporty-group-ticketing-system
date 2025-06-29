package com.sporty.group.agentassignmentservice.integration;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.repository.AgentRepository;
import com.sporty.group.agentassignmentservice.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ApiIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private List<Agent> testAgents;
    private List<Ticket> testTickets;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        ticketRepository.deleteAll();
        agentRepository.deleteAll();

        // Create test agents
        testAgents = new ArrayList<>();
        
        Agent agent1 = new Agent();
        agent1.setAgentId(UUID.randomUUID());
        agent1.setFirstName("John");
        agent1.setLastName("Doe");
        agent1.setAvailability(Agent.AgentAvailability.AVAILABLE);
        agent1.setSkills(new HashSet<>(Arrays.asList("Java", "Spring")));
        
        Agent agent2 = new Agent();
        agent2.setAgentId(UUID.randomUUID());
        agent2.setFirstName("Jane");
        agent2.setLastName("Smith");
        agent2.setAvailability(Agent.AgentAvailability.AVAILABLE);
        agent2.setSkills(new HashSet<>(Arrays.asList("Python", "Django")));
        
        Agent agent3 = new Agent();
        agent3.setAgentId(UUID.randomUUID());
        agent3.setFirstName("Bob");
        agent3.setLastName("Johnson");
        agent3.setAvailability(Agent.AgentAvailability.NOT_AVAILABLE);
        agent3.setSkills(new HashSet<>(Arrays.asList("JavaScript", "React")));
        
        testAgents.add(agentRepository.save(agent1));
        testAgents.add(agentRepository.save(agent2));
        testAgents.add(agentRepository.save(agent3));

        // Create test tickets
        testTickets = new ArrayList<>();
        
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId(UUID.randomUUID());
        ticket1.setSubject("Test Ticket 1");
        ticket1.setDescription("This is a test ticket");
        ticket1.setStatus(Ticket.TicketStatus.OPEN);
        ticket1.setUserId("user123");
        ticket1.setCreatedAt(LocalDateTime.now());
        
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId(UUID.randomUUID());
        ticket2.setSubject("Test Ticket 2");
        ticket2.setDescription("This is another test ticket");
        ticket2.setStatus(Ticket.TicketStatus.OPEN);
        ticket2.setUserId("user456");
        ticket2.setCreatedAt(LocalDateTime.now());
        
        Ticket ticket3 = new Ticket();
        ticket3.setTicketId(UUID.randomUUID());
        ticket3.setSubject("Test Ticket 3");
        ticket3.setDescription("This is a closed ticket");
        ticket3.setStatus(Ticket.TicketStatus.CLOSED);
        ticket3.setUserId("user789");
        ticket3.setCreatedAt(LocalDateTime.now());
        
        testTickets.add(ticketRepository.save(ticket1));
        testTickets.add(ticketRepository.save(ticket2));
        testTickets.add(ticketRepository.save(ticket3));
    }

    @Test
    void testGetAllAgents() {
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/agents";
        
        // Make the request
        ResponseEntity<List<Agent>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Agent>>() {});
        
        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        
        // Verify the agents are returned
        List<Agent> agents = response.getBody();
        assertEquals(3, agents.size());
        
        // Verify agent details
        assertTrue(agents.stream().anyMatch(a -> a.getFirstName().equals("John")));
        assertTrue(agents.stream().anyMatch(a -> a.getFirstName().equals("Jane")));
        assertTrue(agents.stream().anyMatch(a -> a.getFirstName().equals("Bob")));
    }

    @Test
    void testGetAvailableAgents() {
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/agents/available";
        
        // Make the request
        ResponseEntity<List<Agent>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Agent>>() {});
        
        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify only available agents are returned
        List<Agent> availableAgents = response.getBody();
        assertEquals(2, availableAgents.size());
        
        // Verify all returned agents are available
        for (Agent agent : availableAgents) {
            assertEquals(Agent.AgentAvailability.AVAILABLE, agent.getAvailability());
        }
        
        // Verify specific agents
        assertTrue(availableAgents.stream().anyMatch(a -> a.getFirstName().equals("John")));
        assertTrue(availableAgents.stream().anyMatch(a -> a.getFirstName().equals("Jane")));
        assertFalse(availableAgents.stream().anyMatch(a -> a.getFirstName().equals("Bob")));
    }

    @Test
    void testGetAllTickets() {
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/tickets";
        
        // Make the request
        ResponseEntity<List<Ticket>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Ticket>>() {});
        
        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify all tickets are returned
        List<Ticket> tickets = response.getBody();
        assertEquals(3, tickets.size());
        
        // Verify ticket details
        assertTrue(tickets.stream().anyMatch(t -> t.getSubject().equals("Test Ticket 1")));
        assertTrue(tickets.stream().anyMatch(t -> t.getSubject().equals("Test Ticket 2")));
        assertTrue(tickets.stream().anyMatch(t -> t.getSubject().equals("Test Ticket 3")));
    }

    @Test
    void testGetNewTickets() {
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/tickets/new";
        
        // Make the request
        ResponseEntity<List<Ticket>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Ticket>>() {});
        
        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify only open tickets are returned
        List<Ticket> newTickets = response.getBody();
        assertEquals(2, newTickets.size());
        
        // Verify all returned tickets are open
        for (Ticket ticket : newTickets) {
            assertEquals(Ticket.TicketStatus.OPEN, ticket.getStatus());
        }
        
        // Verify specific tickets
        assertTrue(newTickets.stream().anyMatch(t -> t.getSubject().equals("Test Ticket 1")));
        assertTrue(newTickets.stream().anyMatch(t -> t.getSubject().equals("Test Ticket 2")));
        assertFalse(newTickets.stream().anyMatch(t -> t.getSubject().equals("Test Ticket 3")));
    }

    @Test
    void testAssignAgentToTicket() {
        // Get an available agent
        Agent availableAgent = testAgents.stream()
                .filter(a -> a.getAvailability() == Agent.AgentAvailability.AVAILABLE)
                .findFirst()
                .orElseThrow();
        
        // Get an open ticket
        Ticket openTicket = testTickets.stream()
                .filter(t -> t.getStatus() == Ticket.TicketStatus.OPEN)
                .findFirst()
                .orElseThrow();
        
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/tickets/" + openTicket.getTicketId() + "/assign/" + availableAgent.getAgentId();
        
        // Make the request
        ResponseEntity<Ticket> response = restTemplate.postForEntity(url, null, Ticket.class);
        
        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify the ticket was updated
        Ticket updatedTicket = response.getBody();
        assertEquals(openTicket.getTicketId(), updatedTicket.getTicketId());
        assertEquals(availableAgent.getAgentId(), updatedTicket.getAssigneeId());
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, updatedTicket.getStatus());
        
        // Verify the agent was updated in the database
        Optional<Agent> updatedAgentOpt = agentRepository.findById(availableAgent.getAgentId());
        assertTrue(updatedAgentOpt.isPresent());
        Agent updatedAgent = updatedAgentOpt.get();
        assertEquals(Agent.AgentAvailability.NOT_AVAILABLE, updatedAgent.getAvailability());
        assertEquals(openTicket.getTicketId(), updatedAgent.getTicketId());
    }

    @Test
    void testAssignAgentToTicket_TicketNotFound() {
        // Get an available agent
        Agent availableAgent = testAgents.stream()
                .filter(a -> a.getAvailability() == Agent.AgentAvailability.AVAILABLE)
                .findFirst()
                .orElseThrow();
        
        // Use a non-existent ticket ID
        UUID nonExistentTicketId = UUID.randomUUID();
        
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/tickets/" + nonExistentTicketId + "/assign/" + availableAgent.getAgentId();
        
        // Make the request
        ResponseEntity<Ticket> response = restTemplate.postForEntity(url, null, Ticket.class);
        
        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAssignAgentToTicket_AgentNotFound() {
        // Get an open ticket
        Ticket openTicket = testTickets.stream()
                .filter(t -> t.getStatus() == Ticket.TicketStatus.OPEN)
                .findFirst()
                .orElseThrow();
        
        // Use a non-existent agent ID
        UUID nonExistentAgentId = UUID.randomUUID();
        
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/tickets/" + openTicket.getTicketId() + "/assign/" + nonExistentAgentId;
        
        // Make the request
        ResponseEntity<Ticket> response = restTemplate.postForEntity(url, null, Ticket.class);
        
        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAssignAgentToTicket_AgentNotAvailable() {
        // Get a non-available agent
        Agent nonAvailableAgent = testAgents.stream()
                .filter(a -> a.getAvailability() == Agent.AgentAvailability.NOT_AVAILABLE)
                .findFirst()
                .orElseThrow();
        
        // Get an open ticket
        Ticket openTicket = testTickets.stream()
                .filter(t -> t.getStatus() == Ticket.TicketStatus.OPEN)
                .findFirst()
                .orElseThrow();
        
        // Build the URL
        String url = "http://localhost:" + port + "/api/v1/tickets/" + openTicket.getTicketId() + "/assign/" + nonAvailableAgent.getAgentId();
        
        // Make the request
        ResponseEntity<Ticket> response = restTemplate.postForEntity(url, null, Ticket.class);
        
        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}