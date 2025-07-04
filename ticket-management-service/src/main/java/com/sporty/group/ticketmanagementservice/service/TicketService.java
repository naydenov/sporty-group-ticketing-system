package com.sporty.group.ticketmanagementservice.service;

import com.sporty.group.sportygroupticketingcommons.event.NewTicketEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketAssignedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketStatusUpdatedEvent;
import com.sporty.group.ticketmanagementservice.model.Ticket;
import com.sporty.group.ticketmanagementservice.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for handling ticket-related operations.
 */
@Service
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final KafkaTemplate<String, NewTicketEvent> newTicketKafkaTemplate;

    private final String ticketCreatedTopic;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public TicketService(TicketRepository ticketRepository, 
                         KafkaTemplate<String, NewTicketEvent> newTicketKafkaTemplate,
                         @org.springframework.beans.factory.annotation.Value("${kafka.topic.ticket-created}") String ticketCreatedTopic) {
        this.ticketRepository = ticketRepository;
        this.newTicketKafkaTemplate = newTicketKafkaTemplate;
        this.ticketCreatedTopic = ticketCreatedTopic;
    }

    /**
     * Process a TicketCreatedEvent by creating a new ticket and sending a NewTicketEvent.
     *
     * @param event the TicketCreatedEvent to process
     */
    public void processTicketCreated(TicketCreatedEvent event) {
        log.info("Processing TicketCreatedEvent: {}", event);

        UUID ticketId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .ticketId(ticketId)
                .subject(event.getSubject())
                .description(event.getDescription())
                .status(Ticket.TicketStatus.OPEN)
                .userId(event.getUserId())
                .createdAt(now)
                .updatedAt(now)
                .build();

        ticketRepository.save(ticket);
        log.info("Ticket created with ID: {}", ticketId);

        // Send NewTicketEvent to Kafka
        NewTicketEvent newTicketEvent = NewTicketEvent.builder()
                .ticketId(ticketId.toString())
                .status(ticket.getStatus().name().toLowerCase())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .createdAt(ticket.getCreatedAt().format(DATE_FORMATTER))
                .build();

        newTicketKafkaTemplate.send(ticketCreatedTopic, newTicketEvent);
        log.info("NewTicketEvent sent to Kafka: {}", newTicketEvent);
    }

    /**
     * Process a TicketAssignedEvent by updating the assignee of a ticket.
     *
     * @param event the TicketAssignedEvent to process
     */
    public void processTicketAssigned(TicketAssignedEvent event) {
        log.info("Processing TicketAssignedEvent: {}", event);

        UUID ticketId = UUID.fromString(event.getTicketId());
        ticketRepository.findById(ticketId).ifPresentOrElse(
            ticket -> {
                ticket.setAssigneeId(event.getAssigneeId());
                ticket.setUpdatedAt(LocalDateTime.now());
                ticketRepository.save(ticket);
                log.info("Ticket {} assigned to {}", ticketId, event.getAssigneeId());
            },
            () -> log.warn("Ticket not found with ID: {}", ticketId)
        );
    }

    /**
     * Process a TicketStatusUpdatedEvent by updating the status of a ticket.
     *
     * @param event the TicketStatusUpdatedEvent to process
     */
    public void processTicketStatusUpdated(TicketStatusUpdatedEvent event) {
        log.info("Processing TicketStatusUpdatedEvent: {}", event);

        UUID ticketId = UUID.fromString(event.getTicketId());
        ticketRepository.findById(ticketId).ifPresentOrElse(
            ticket -> {
                try {
                    // Try to parse the status
                    Ticket.TicketStatus newStatus = Ticket.TicketStatus.valueOf(event.getStatus().toUpperCase());

                    // Only update and save if status is valid
                    ticket.setStatus(newStatus);
                    ticket.setUpdatedAt(LocalDateTime.now());
                    ticketRepository.save(ticket);
                    log.info("Ticket {} status updated to {}", ticketId, newStatus);
                } catch (IllegalArgumentException e) {
                    // Log error but don't update the ticket for invalid status
                    log.error("Invalid status value: {}", event.getStatus(), e);
                }
            },
            () -> log.warn("Ticket not found with ID: {}", ticketId)
        );
    }
}
