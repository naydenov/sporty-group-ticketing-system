package com.sporty.group.agentassignmentservice.controller;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Agent", description = "Agent management APIs")
public class AgentController {

    private final AgentService agentService;

    @Operation(summary = "Get all agents", description = "Retrieves a list of all agents in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of agents",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Agent.class)))
    })
    @GetMapping
    public ResponseEntity<List<Agent>> getAllAgents() {
        log.info("Request to get all agents");
        return ResponseEntity.ok(agentService.getAllAgents());
    }

    @Operation(summary = "Get available agents", description = "Retrieves a list of all available agents")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of available agents",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Agent.class)))
    })
    @GetMapping("/available")
    public ResponseEntity<List<Agent>> getAvailableAgents() {
        log.info("Request to get available agents");
        return ResponseEntity.ok(agentService.getAvailableAgents());
    }
}
