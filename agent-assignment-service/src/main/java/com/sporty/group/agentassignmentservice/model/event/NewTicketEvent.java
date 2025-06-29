package com.sporty.group.agentassignmentservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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