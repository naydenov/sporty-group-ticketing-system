package com.sporty.group.ticketmanagementservice.listener;

import com.sporty.group.ticketmanagementservice.model.event.TicketAssignedEvent;
import com.sporty.group.ticketmanagementservice.model.event.TicketCreatedEvent;
import com.sporty.group.ticketmanagementservice.model.event.TicketStatusUpdatedEvent;
import com.sporty.group.ticketmanagementservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for processing ticket-related events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {

    private final TicketService ticketService;

    /**
     * Listen for TicketCreatedEvent on the support-tickets topic.
     *
     * @param event the TicketCreatedEvent to process
     */
    @KafkaListener(
            topics = "${kafka.topic.support-tickets}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "ticketCreatedKafkaListenerContainerFactory"
    )
    public void listenTicketCreated(TicketCreatedEvent event) {
        log.info("Received TicketCreatedEvent: {}", event);
        ticketService.processTicketCreated(event);
    }

    /**
     * Listen for TicketAssignedEvent on the ticket-assignments topic.
     *
     * @param event the TicketAssignedEvent to process
     */
    @KafkaListener(
            topics = "${kafka.topic.ticket-assignments}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "ticketAssignedKafkaListenerContainerFactory"
    )
    public void listenTicketAssigned(TicketAssignedEvent event) {
        log.info("Received TicketAssignedEvent: {}", event);
        ticketService.processTicketAssigned(event);
    }

    /**
     * Listen for TicketStatusUpdatedEvent on the ticket-updates topic.
     *
     * @param event the TicketStatusUpdatedEvent to process
     */
    @KafkaListener(
            topics = "${kafka.topic.ticket-updates}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "ticketStatusUpdatedKafkaListenerContainerFactory"
    )
    public void listenTicketStatusUpdated(TicketStatusUpdatedEvent event) {
        log.info("Received TicketStatusUpdatedEvent: {}", event);
        ticketService.processTicketStatusUpdated(event);
    }
}