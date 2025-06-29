package com.sporty.group.ticketmanagementservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event representing a ticket assignment.
 * This event is consumed from the 'ticket-assignments' topic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssignedEvent {
    private String ticketId;
    private String assigneeId;
}