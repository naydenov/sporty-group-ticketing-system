package com.sporty.group.agentassignmentservice.config;

import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final AgentRepository agentRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing sample data...");

            // Create sample agents
            Agent agent1 = new Agent();
            agent1.setAgentId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            agent1.setFirstName("John");
            agent1.setLastName("Doe");
            agent1.setAvailability(Agent.AgentAvailability.AVAILABLE);
            agent1.setSkills(Set.of("Java", "Spring", "Kafka"));

            Agent agent2 = new Agent();
            agent2.setAgentId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
            agent2.setFirstName("Jane");
            agent2.setLastName("Smith");
            agent2.setAvailability(Agent.AgentAvailability.AVAILABLE);
            agent2.setSkills(Set.of("JavaScript", "React", "Node.js"));

            Agent agent3 = new Agent();
            agent3.setAgentId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
            agent3.setFirstName("Bob");
            agent3.setLastName("Johnson");
            agent3.setAvailability(Agent.AgentAvailability.NOT_AVAILABLE);
            agent3.setSkills(Set.of("Python", "Django", "Flask"));
            agent3.setTicketId(UUID.fromString("99999999-9999-9999-9999-999999999999"));

            // Save agents
            agentRepository.saveAll(Set.of(agent1, agent2, agent3));

            log.info("Sample data initialized with {} agents", agentRepository.count());
        };
    }
}