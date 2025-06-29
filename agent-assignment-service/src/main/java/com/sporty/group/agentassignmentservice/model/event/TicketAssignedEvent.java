package com.sporty.group.agentassignmentservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssignedEvent {
    private String ticketId;
    private String assigneeId;
}