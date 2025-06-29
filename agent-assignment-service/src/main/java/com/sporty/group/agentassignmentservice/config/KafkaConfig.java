package com.sporty.group.agentassignmentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TICKET_CREATED_TOPIC = "ticket-created";
    public static final String TICKET_ASSIGNMENTS_TOPIC = "ticket-assignments";

    @Bean
    public NewTopic ticketCreatedTopic() {
        return TopicBuilder.name(TICKET_CREATED_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ticketAssignmentsTopic() {
        return TopicBuilder.name(TICKET_ASSIGNMENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}