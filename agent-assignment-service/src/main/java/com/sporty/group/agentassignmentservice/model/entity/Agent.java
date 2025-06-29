package com.sporty.group.agentassignmentservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    
    @Id
    private UUID agentId;
    
    private String firstName;
    
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    private AgentAvailability availability;
    
    @ElementCollection
    @CollectionTable(name = "agent_skills", joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "skill")
    private Set<String> skills;
    
    private UUID ticketId;
    
    public enum AgentAvailability {
        AVAILABLE,
        NOT_AVAILABLE
    }
}