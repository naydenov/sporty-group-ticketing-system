package com.sporty.group.ticketapigatewayservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new ticket")
public class TicketRequest {
    @Schema(description = "ID of the user creating the ticket", example = "user-001")
    private String userId;

    @Schema(description = "Subject/title of the ticket", example = "Login problem")
    private String subject;

    @Schema(description = "Detailed description of the issue", example = "Cannot reset my password")
    private String description;
}
