package com.sporty.group.ticketmanagementservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;

@TestConfiguration
@EmbeddedKafka(
        partitions = 1,
        topics = {
                "support-tickets",
                "ticket-assignments",
                "ticket-updates",
                "ticket-created"
        },
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        controlledShutdown = true
)
public class TestKafkaConfig {
    // Configuration class for embedded Kafka broker in tests

    @Bean
    public KafkaAdmin.NewTopics testTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name("support-tickets").partitions(1).replicas(1).build(),
                TopicBuilder.name("ticket-assignments").partitions(1).replicas(1).build(),
                TopicBuilder.name("ticket-updates").partitions(1).replicas(1).build(),
                TopicBuilder.name("ticket-created").partitions(1).replicas(1).build()
        );
    }
}
