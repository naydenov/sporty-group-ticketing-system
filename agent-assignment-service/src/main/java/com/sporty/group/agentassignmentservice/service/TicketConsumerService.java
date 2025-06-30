package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.config.KafkaConfig;
import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.model.event.NewTicketEvent;
import com.sporty.group.agentassignmentservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketConsumerService {

    private final TicketRepository ticketRepository;

    @KafkaListener(topics = KafkaConfig.TICKET_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "newTicketKafkaListenerContainerFactory")
    public void consumeNewTicketEvent(NewTicketEvent event) {
        log.info("Received new ticket event: {}", event);

        try {
            Ticket ticket = new Ticket();
            ticket.setTicketId(UUID.fromString(event.getTicketId()));
            ticket.setSubject(event.getSubject());
            ticket.setDescription(event.getDescription());
            ticket.setStatus(Ticket.TicketStatus.valueOf(event.getStatus().toUpperCase()));
            ticket.setUserId(event.getUserId());

            // Parse createdAt if provided
            if (event.getCreatedAt() != null && !event.getCreatedAt().isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDateTime createdAt = LocalDateTime.parse(event.getCreatedAt(), formatter);
                    ticket.setCreatedAt(createdAt);
                } catch (Exception e) {
                    log.warn("Could not parse createdAt date: {}, using current time", event.getCreatedAt());
                    ticket.setCreatedAt(LocalDateTime.now());
                }
            } else {
                ticket.setCreatedAt(LocalDateTime.now());
            }

            ticketRepository.save(ticket);
            log.info("Saved new ticket with ID: {}", ticket.getTicketId());
        } catch (Exception e) {
            log.error("Error processing new ticket event: {}", e.getMessage(), e);
        }
    }
}
