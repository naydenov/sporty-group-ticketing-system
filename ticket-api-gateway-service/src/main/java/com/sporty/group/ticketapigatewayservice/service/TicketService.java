package com.sporty.group.ticketapigatewayservice.service;

import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import com.sporty.group.ticketapigatewayservice.dto.TicketRequest;
import com.sporty.group.sportygroupticketingcommons.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final KafkaTemplate<String, TicketCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "support-tickets";

    public Ticket createTicket(TicketRequest ticketRequest) {
        // Create a new ticket with default values
        LocalDateTime now = LocalDateTime.now();
        Ticket ticket = Ticket.builder()
                .ticketId(UUID.randomUUID())
                .subject(ticketRequest.getSubject())
                .description(ticketRequest.getDescription())
                .status(Ticket.TicketStatus.OPEN)
                .userId(ticketRequest.getUserId())
                .assigneeId(null) // Initially no assignee
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Create and send the ticket created event
        TicketCreatedEvent event = TicketCreatedEvent.fromTicket(ticket);
        try {
            kafkaTemplate.send(TOPIC, ticket.getTicketId().toString(), event);
            log.info("Ticket created event sent for ticket ID: {}", ticket.getTicketId());
        } catch (Exception ex) {
            log.error("Failed to send ticket created event for ticket ID: {}", ticket.getTicketId(), ex);
        }

        return ticket;
    }
}
