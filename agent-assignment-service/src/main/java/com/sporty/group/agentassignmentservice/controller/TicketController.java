package com.sporty.group.agentassignmentservice.controller;

import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ticket", description = "Ticket management APIs")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Get all tickets", description = "Retrieves a list of all tickets in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of tickets",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Ticket.class)))
    })
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        log.info("Request to get all tickets");
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @Operation(summary = "Get new tickets", description = "Retrieves a list of all new tickets that haven't been assigned")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of new tickets",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Ticket.class)))
    })
    @GetMapping("/new")
    public ResponseEntity<List<Ticket>> getNewTickets() {
        log.info("Request to get new tickets");
        return ResponseEntity.ok(ticketService.getNewTickets());
    }

    @Operation(summary = "Assign agent to ticket", description = "Assigns a specific agent to a specific ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully assigned agent to ticket",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Ticket.class))),
        @ApiResponse(responseCode = "400", description = "Invalid ticket or agent ID supplied",
                content = @Content)
    })
    @PostMapping("/{ticketId}/assign/{agentId}")
    public ResponseEntity<Ticket> assignAgentToTicket(
            @Parameter(description = "ID of the ticket to be assigned") @PathVariable UUID ticketId,
            @Parameter(description = "ID of the agent to assign to the ticket") @PathVariable UUID agentId) {
        log.info("Request to assign agent {} to ticket {}", agentId, ticketId);
        try {
            Ticket updatedTicket = ticketService.assignAgentToTicket(ticketId, agentId);
            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            log.error("Error assigning agent to ticket: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
