package com.sporty.group.agentassignmentservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for integration tests.
 * Sets up the test environment with embedded Kafka and H2 database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"ticket-created", "ticket-assignments"})
public abstract class AbstractIntegrationTest {
    // No additional setup needed, Spring Boot will configure the embedded Kafka broker
}
