package com.sporty.group.sportygroupticketingcommons.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Ticket entity representing a support request")
public class Ticket {
    @Schema(description = "Unique identifier for the ticket", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID ticketId;

    @Schema(description = "Subject/title of the ticket", example = "Login problem")
    private String subject;

    @Schema(description = "Detailed description of the issue", example = "Cannot reset my password")
    private String description;

    @Schema(description = "Current status of the ticket", example = "OPEN")
    private TicketStatus status;

    @Schema(description = "ID of the user who created the ticket", example = "user-001")
    private String userId;

    @Schema(description = "ID of the support agent assigned to the ticket", example = "agent-001")
    private String assigneeId;

    @Schema(description = "Timestamp when the ticket was created", example = "2023-09-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the ticket was last updated", example = "2023-09-15T14:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Possible statuses for a ticket")
    public enum TicketStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }
}
