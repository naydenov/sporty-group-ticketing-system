package com.sporty.group.agentassignmentservice.integration;

import com.sporty.group.agentassignmentservice.config.KafkaConfig;
import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.repository.AgentRepository;
import com.sporty.group.agentassignmentservice.repository.TicketRepository;
import com.sporty.group.sportygroupticketingcommons.event.NewTicketEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketAssignedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AgentAssignmentIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private KafkaMessageListenerContainer<String, TicketAssignedEvent> container;
    private BlockingQueue<ConsumerRecord<String, TicketAssignedEvent>> records;
    private Producer<String, NewTicketEvent> producer;

    @Autowired
    private org.springframework.core.env.Environment environment;

    @BeforeEach
    void setUp() {
        // Get the embedded Kafka broker address
        String brokers = environment.getProperty("spring.embedded.kafka.brokers");

        // Set up Kafka consumer for ticket-assignments topic
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        DefaultKafkaConsumerFactory<String, TicketAssignedEvent> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps, new StringDeserializer(),
                new JsonDeserializer<>(TicketAssignedEvent.class, false));

        ContainerProperties containerProperties = new ContainerProperties(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC);
        container = new KafkaMessageListenerContainer<>(cf, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, TicketAssignedEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, 1);

        // Set up Kafka producer for ticket-created topic
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(brokers);
        DefaultKafkaProducerFactory<String, NewTicketEvent> pf = new DefaultKafkaProducerFactory<>(
                producerProps, new StringSerializer(), new JsonSerializer<>());
        producer = pf.createProducer();

        // Clean up repositories
        ticketRepository.deleteAll();
        agentRepository.deleteAll();

        // Create test agent
        Agent agent = new Agent();
        agent.setAgentId(UUID.randomUUID());
        agent.setFirstName("Test");
        agent.setLastName("Agent");
        agent.setAvailability(Agent.AgentAvailability.AVAILABLE);
        agent.setSkills(new HashSet<>(Arrays.asList("Java", "Spring")));
        agentRepository.save(agent);
    }

    @AfterEach
    void tearDown() {
        container.stop();
        producer.close();
    }

    @Test
    void testEndToEndTicketAssignmentFlow() throws Exception {
        // 1. Create a new ticket event
        UUID ticketId = UUID.randomUUID();
        NewTicketEvent newTicketEvent = new NewTicketEvent(
                ticketId.toString(),
                "user123",
                "OPEN",
                "Test Subject",
                "Test Description",
                "15.03.2025"
        );

        // 2. Send the new ticket event to Kafka
        producer.send(new ProducerRecord<>(KafkaConfig.TICKET_CREATED_TOPIC, newTicketEvent)).get();

        // 3. Wait for the ticket to be processed and saved
        Thread.sleep(2000); // Give some time for the consumer to process the message

        // 4. Verify the ticket was created in the database
        Optional<Ticket> savedTicket = ticketRepository.findById(ticketId);
        assertTrue(savedTicket.isPresent(), "Ticket should be saved in the database");
        assertEquals("Test Subject", savedTicket.get().getSubject());
        assertEquals(Ticket.TicketStatus.OPEN, savedTicket.get().getStatus());

        // 5. Get all available agents
        String url = "http://localhost:" + port + "/api/v1/agents/available";
        ResponseEntity<List<Agent>> agentsResponse = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Agent>>() {});

        assertEquals(HttpStatus.OK, agentsResponse.getStatusCode());
        assertFalse(agentsResponse.getBody().isEmpty(), "There should be available agents");

        Agent availableAgent = agentsResponse.getBody().get(0);

        // 6. Assign the agent to the ticket
        String assignUrl = "http://localhost:" + port + "/api/v1/tickets/" + ticketId + "/assign/" + availableAgent.getAgentId();
        ResponseEntity<Ticket> assignResponse = restTemplate.postForEntity(assignUrl, null, Ticket.class);

        assertEquals(HttpStatus.OK, assignResponse.getStatusCode());
        assertNotNull(assignResponse.getBody());
        assertEquals(availableAgent.getAgentId(), assignResponse.getBody().getAssigneeId());
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, assignResponse.getBody().getStatus());

        // 7. Verify the agent's availability was updated
        Optional<Agent> updatedAgent = agentRepository.findById(availableAgent.getAgentId());
        assertTrue(updatedAgent.isPresent());
        assertEquals(Agent.AgentAvailability.NOT_AVAILABLE, updatedAgent.get().getAvailability());
        assertEquals(ticketId, updatedAgent.get().getTicketId());

        // 8. Verify a TicketAssignedEvent was sent to Kafka
        ConsumerRecord<String, TicketAssignedEvent> record = records.poll(5, TimeUnit.SECONDS);
        assertNotNull(record);
        assertEquals(ticketId.toString(), record.value().getTicketId());
        assertEquals(availableAgent.getAgentId().toString(), record.value().getAssigneeId());
    }
}
