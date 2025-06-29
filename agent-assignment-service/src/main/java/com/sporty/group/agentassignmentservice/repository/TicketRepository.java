package com.sporty.group.agentassignmentservice.repository;

import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByStatus(Ticket.TicketStatus status);
}