package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final AgentRepository agentRepository;

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public List<Agent> getAvailableAgents() {
        return agentRepository.findByAvailability(Agent.AgentAvailability.AVAILABLE);
    }

    public Optional<Agent> getAgentById(UUID agentId) {
        return agentRepository.findById(agentId);
    }

    public Agent updateAgentAvailability(UUID agentId, Agent.AgentAvailability availability, UUID ticketId) {
        return agentRepository.findById(agentId)
                .map(agent -> {
                    agent.setAvailability(availability);
                    agent.setTicketId(ticketId);
                    return agentRepository.save(agent);
                })
                .orElseThrow(() -> new RuntimeException("Agent not found with ID: " + agentId));
    }
}