package com.sporty.group.ticketmanagementservice.controller;

import com.sporty.group.ticketmanagementservice.model.Ticket;
import com.sporty.group.ticketmanagementservice.model.event.TicketCreatedEvent;
import com.sporty.group.ticketmanagementservice.model.event.TicketStatusUpdatedEvent;
import com.sporty.group.ticketmanagementservice.repository.TicketRepository;
import com.sporty.group.ticketmanagementservice.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for ticket management operations.
 */
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket Management", description = "API for managing support tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    /**
     * Create a new ticket.
     *
     * @param ticketRequest the ticket creation request
     * @return the created ticket
     */
    @PostMapping
    @Operation(summary = "Create a new ticket", description = "Creates a new support ticket in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Map<String, String>> createTicket(
            @Parameter(description = "Ticket creation request", required = true)
            @RequestBody TicketCreatedEvent ticketRequest) {

        ticketService.processTicketCreated(ticketRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Ticket created successfully"));
    }

    /**
     * Get a ticket by ID.
     *
     * @param ticketId the ID of the ticket to retrieve
     * @return the ticket if found
     */
    @GetMapping("/{ticketId}")
    @Operation(summary = "Get a ticket by ID", description = "Retrieves a ticket by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<Ticket> getTicket(
            @Parameter(description = "ID of the ticket to retrieve", required = true)
            @PathVariable UUID ticketId) {

        return ticketRepository.findById(ticketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update the status of a ticket.
     *
     * @param ticketId the ID of the ticket to update
     * @param statusRequest the status update request
     * @return a success message if the update was successful
     */
    @PutMapping("/{ticketId}/status")
    @Operation(summary = "Update ticket status", description = "Updates the status of an existing ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<Map<String, String>> updateTicketStatus(
            @Parameter(description = "ID of the ticket to update", required = true)
            @PathVariable String ticketId,
            @Parameter(description = "Status update request", required = true)
            @RequestBody Map<String, String> statusRequest) {

        if (!ticketRepository.existsById(UUID.fromString(ticketId))) {
            return ResponseEntity.notFound().build();
        }

        TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent();
        event.setTicketId(ticketId);
        event.setStatus(statusRequest.get("status"));

        ticketService.processTicketStatusUpdated(event);

        return ResponseEntity.ok(Map.of("message", "Ticket status updated successfully"));
    }

    /**
     * Get all tickets.
     *
     * @return a list of all tickets
     */
    @GetMapping
    @Operation(summary = "Get all tickets", description = "Retrieves a list of all tickets in the system")
    @ApiResponse(responseCode = "200", description = "List of tickets retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
    public ResponseEntity<Map<String, Ticket>> getAllTickets() {
        Map<String, Ticket> tickets = ticketRepository.findAll().stream()
                .collect(Collectors.toMap(
                    ticket -> ticket.getTicketId().toString(), 
                    ticket -> ticket
                ));

        return ResponseEntity.ok(tickets);
    }
}
