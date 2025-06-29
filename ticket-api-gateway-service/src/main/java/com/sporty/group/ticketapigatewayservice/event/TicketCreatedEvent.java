package com.sporty.group.ticketapigatewayservice.event;

import com.sporty.group.ticketapigatewayservice.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreatedEvent {
    private UUID ticketId;
    private String subject;
    private String description;
    private Ticket.TicketStatus status;
    private String userId;
    private String assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TicketCreatedEvent fromTicket(Ticket ticket) {
        return TicketCreatedEvent.builder()
                .ticketId(ticket.getTicketId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .userId(ticket.getUserId())
                .assigneeId(ticket.getAssigneeId())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}