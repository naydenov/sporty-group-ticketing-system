package com.sporty.group.ticketmanagementservice.repository;

import com.sporty.group.ticketmanagementservice.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * JPA repository for storing and retrieving tickets.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}
