package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.repository.AgentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @InjectMocks
    private AgentService agentService;

    private UUID agentId;
    private Agent agent;
    private List<Agent> agents;

    @BeforeEach
    void setUp() {
        agentId = UUID.randomUUID();
        agent = new Agent();
        agent.setAgentId(agentId);
        agent.setFirstName("John");
        agent.setLastName("Doe");
        agent.setAvailability(Agent.AgentAvailability.AVAILABLE);
        agent.setSkills(new HashSet<>(Arrays.asList("Java", "Spring")));
        
        Agent agent2 = new Agent();
        agent2.setAgentId(UUID.randomUUID());
        agent2.setFirstName("Jane");
        agent2.setLastName("Smith");
        agent2.setAvailability(Agent.AgentAvailability.NOT_AVAILABLE);
        agent2.setSkills(new HashSet<>(Arrays.asList("Python", "Django")));
        
        agents = Arrays.asList(agent, agent2);
    }

    @Test
    void getAllAgents_ShouldReturnAllAgents() {
        // Arrange
        when(agentRepository.findAll()).thenReturn(agents);

        // Act
        List<Agent> result = agentService.getAllAgents();

        // Assert
        assertEquals(2, result.size());
        verify(agentRepository, times(1)).findAll();
    }

    @Test
    void getAvailableAgents_ShouldReturnOnlyAvailableAgents() {
        // Arrange
        when(agentRepository.findByAvailability(Agent.AgentAvailability.AVAILABLE))
                .thenReturn(Collections.singletonList(agent));

        // Act
        List<Agent> result = agentService.getAvailableAgents();

        // Assert
        assertEquals(1, result.size());
        assertEquals(Agent.AgentAvailability.AVAILABLE, result.get(0).getAvailability());
        verify(agentRepository, times(1)).findByAvailability(Agent.AgentAvailability.AVAILABLE);
    }

    @Test
    void getAgentById_WhenAgentExists_ShouldReturnAgent() {
        // Arrange
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));

        // Act
        Optional<Agent> result = agentService.getAgentById(agentId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(agentId, result.get().getAgentId());
        verify(agentRepository, times(1)).findById(agentId);
    }

    @Test
    void getAgentById_WhenAgentDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(agentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Agent> result = agentService.getAgentById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
        verify(agentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void updateAgentAvailability_WhenAgentExists_ShouldUpdateAndReturnAgent() {
        // Arrange
        UUID ticketId = UUID.randomUUID();
        Agent updatedAgent = new Agent();
        updatedAgent.setAgentId(agentId);
        updatedAgent.setFirstName("John");
        updatedAgent.setLastName("Doe");
        updatedAgent.setAvailability(Agent.AgentAvailability.NOT_AVAILABLE);
        updatedAgent.setTicketId(ticketId);
        updatedAgent.setSkills(new HashSet<>(Arrays.asList("Java", "Spring")));
        
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));
        when(agentRepository.save(any(Agent.class))).thenReturn(updatedAgent);

        // Act
        Agent result = agentService.updateAgentAvailability(agentId, Agent.AgentAvailability.NOT_AVAILABLE, ticketId);

        // Assert
        assertEquals(Agent.AgentAvailability.NOT_AVAILABLE, result.getAvailability());
        assertEquals(ticketId, result.getTicketId());
        verify(agentRepository, times(1)).findById(agentId);
        verify(agentRepository, times(1)).save(any(Agent.class));
    }

    @Test
    void updateAgentAvailability_WhenAgentDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        when(agentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            agentService.updateAgentAvailability(nonExistentId, Agent.AgentAvailability.NOT_AVAILABLE, ticketId)
        );
        
        assertEquals("Agent not found with ID: " + nonExistentId, exception.getMessage());
        verify(agentRepository, times(1)).findById(nonExistentId);
        verify(agentRepository, never()).save(any(Agent.class));
    }
}