package com.sporty.group.ticketmanagementservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a support ticket in the system.
 */
@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    private UUID ticketId;
    private String subject;
    private String description;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    private String userId;
    private String assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Enum representing the possible statuses of a ticket.
     */
    public enum TicketStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }
}
