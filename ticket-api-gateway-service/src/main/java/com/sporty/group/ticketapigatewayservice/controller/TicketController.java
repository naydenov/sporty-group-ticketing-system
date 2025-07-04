package com.sporty.group.ticketapigatewayservice.controller;

import com.sporty.group.ticketapigatewayservice.dto.TicketRequest;
import com.sporty.group.sportygroupticketingcommons.model.Ticket;
import com.sporty.group.ticketapigatewayservice.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Ticket Management", description = "APIs for managing support tickets")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Create a new ticket", description = "Creates a new support ticket with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ticket created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Ticket.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/tickets")
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequest ticketRequest) {
        Ticket createdTicket = ticketService.createTicket(ticketRequest);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }
}
