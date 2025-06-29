package com.sporty.group.agentassignmentservice.controller;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.service.AgentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AgentControllerTest {

    @Mock
    private AgentService agentService;

    @InjectMocks
    private AgentController agentController;

    private MockMvc mockMvc;
    private List<Agent> agents;
    private List<Agent> availableAgents;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(agentController).build();

        // Create test agents
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
        agent2.setAvailability(Agent.AgentAvailability.NOT_AVAILABLE);
        agent2.setSkills(new HashSet<>(Arrays.asList("Python", "Django")));

        agents = Arrays.asList(agent1, agent2);
        availableAgents = Collections.singletonList(agent1);
    }

    @Test
    void getAllAgents_ShouldReturnAllAgents() throws Exception {
        // Arrange
        when(agentService.getAllAgents()).thenReturn(agents);

        // Act & Assert
        mockMvc.perform(get("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].availability", is("AVAILABLE")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")))
                .andExpect(jsonPath("$[1].lastName", is("Smith")))
                .andExpect(jsonPath("$[1].availability", is("NOT_AVAILABLE")));

        verify(agentService, times(1)).getAllAgents();
    }

    @Test
    void getAvailableAgents_ShouldReturnOnlyAvailableAgents() throws Exception {
        // Arrange
        when(agentService.getAvailableAgents()).thenReturn(availableAgents);

        // Act & Assert
        mockMvc.perform(get("/api/v1/agents/available")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].availability", is("AVAILABLE")));

        verify(agentService, times(1)).getAvailableAgents();
    }

    @Test
    void getAllAgents_WhenNoAgents_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(agentService.getAllAgents()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(agentService, times(1)).getAllAgents();
    }

    @Test
    void getAvailableAgents_WhenNoAvailableAgents_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(agentService.getAvailableAgents()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/agents/available")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(agentService, times(1)).getAvailableAgents();
    }
}