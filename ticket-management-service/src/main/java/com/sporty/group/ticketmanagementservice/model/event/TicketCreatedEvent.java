package com.sporty.group.ticketmanagementservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event representing a new ticket creation request.
 * This event is consumed from the 'support-tickets' topic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreatedEvent {
    private String userId;
    private String subject;
    private String description;
}