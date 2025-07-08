package com.sporty.group.sportygroupticketingcommons.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event representing a newly created ticket.
 * This event is produced to the 'ticket-created' topic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewTicketEvent {
    private String ticketId;
    private String userId;
    private String status;
    private String subject;
    private String description;
    private String createdAt;
}