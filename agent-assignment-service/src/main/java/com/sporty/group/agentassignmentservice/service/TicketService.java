package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AgentService agentService;
    private final TicketProducerService ticketProducerService;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getNewTickets() {
        return ticketRepository.findByStatus(Ticket.TicketStatus.OPEN);
    }

    public Optional<Ticket> getTicketById(UUID ticketId) {
        return ticketRepository.findById(ticketId);
    }

    @Transactional
    public Ticket assignAgentToTicket(UUID ticketId, UUID agentId) {
        log.info("Assigning agent {} to ticket {}", agentId, ticketId);
        
        // Get the ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        // Check if ticket is already assigned
        if (ticket.getAssigneeId() != null) {
            throw new RuntimeException("Ticket is already assigned to an agent");
        }
        
        // Get the agent
        Agent agent = agentService.getAgentById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found with ID: " + agentId));
        
        // Check if agent is available
        if (agent.getAvailability() != Agent.AgentAvailability.AVAILABLE) {
            throw new RuntimeException("Agent is not available");
        }
        
        // Update ticket
        ticket.setAssigneeId(agentId);
        ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        // Update agent
        agentService.updateAgentAvailability(agentId, Agent.AgentAvailability.NOT_AVAILABLE, ticketId);
        
        // Send Kafka event
        ticketProducerService.sendTicketAssignedEvent(ticketId, agentId);
        
        return updatedTicket;
    }
}