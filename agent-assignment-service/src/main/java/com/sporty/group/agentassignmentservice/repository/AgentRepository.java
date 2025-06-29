package com.sporty.group.agentassignmentservice.repository;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {
    List<Agent> findByAvailability(Agent.AgentAvailability availability);
}