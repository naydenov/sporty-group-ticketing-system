package com.sporty.group.sportygroupticketingcommons.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event representing a ticket status update.
 * This event is consumed from the 'ticket-updates' topic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusUpdatedEvent {
    private String ticketId;
    private String status;
}