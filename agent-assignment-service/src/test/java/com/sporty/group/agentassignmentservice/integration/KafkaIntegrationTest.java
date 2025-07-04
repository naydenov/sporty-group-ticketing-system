package com.sporty.group.agentassignmentservice.integration;

import com.sporty.group.agentassignmentservice.config.KafkaConfig;
import com.sporty.group.agentassignmentservice.model.entity.Agent;
import com.sporty.group.agentassignmentservice.model.entity.Ticket;
import com.sporty.group.agentassignmentservice.repository.AgentRepository;
import com.sporty.group.agentassignmentservice.repository.TicketRepository;
import com.sporty.group.agentassignmentservice.service.TicketService;
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

import static org.junit.jupiter.api.Assertions.*;

class KafkaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    private KafkaMessageListenerContainer<String, TicketAssignedEvent> assignmentContainer;
    private BlockingQueue<ConsumerRecord<String, TicketAssignedEvent>> assignmentRecords;

    private Producer<String, NewTicketEvent> ticketProducer;

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
        assignmentContainer = new KafkaMessageListenerContainer<>(cf, containerProperties);
        assignmentRecords = new LinkedBlockingQueue<>();
        assignmentContainer.setupMessageListener((MessageListener<String, TicketAssignedEvent>) assignmentRecords::add);
        assignmentContainer.start();
        ContainerTestUtils.waitForAssignment(assignmentContainer, 1);

        // Set up Kafka producer for ticket-created topic
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(brokers);
        DefaultKafkaProducerFactory<String, NewTicketEvent> pf = new DefaultKafkaProducerFactory<>(
                producerProps, new StringSerializer(), new JsonSerializer<>());
        ticketProducer = pf.createProducer();

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
        assignmentContainer.stop();
        ticketProducer.close();
    }

    @Test
    void testConsumeNewTicketEvent() throws Exception {
        // Create a new ticket event
        UUID ticketId = UUID.randomUUID();
        NewTicketEvent newTicketEvent = new NewTicketEvent(
                ticketId.toString(),
                "user123",
                "OPEN",
                "Test Subject",
                "Test Description",
                "15.03.2025"
        );

        // Send the new ticket event to Kafka
        ticketProducer.send(new ProducerRecord<>(KafkaConfig.TICKET_CREATED_TOPIC, newTicketEvent)).get();

        // Wait for the ticket to be processed and saved
        Thread.sleep(2000); // Give some time for the consumer to process the message

        // Verify the ticket was created in the database
        Optional<Ticket> savedTicket = ticketRepository.findById(ticketId);
        assertTrue(savedTicket.isPresent(), "Ticket should be saved in the database");
        assertEquals("Test Subject", savedTicket.get().getSubject());
        assertEquals("Test Description", savedTicket.get().getDescription());
        assertEquals(Ticket.TicketStatus.OPEN, savedTicket.get().getStatus());
        assertEquals("user123", savedTicket.get().getUserId());
    }

    @Test
    void testProduceTicketAssignedEvent() throws Exception {
        // Create a ticket directly in the database
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketId);
        ticket.setSubject("Test Subject");
        ticket.setDescription("Test Description");
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        ticket.setUserId("user123");
        ticket.setCreatedAt(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        ticketRepository.save(ticket);

        // Get an available agent
        List<Agent> availableAgents = agentRepository.findByAvailability(Agent.AgentAvailability.AVAILABLE);
        assertFalse(availableAgents.isEmpty(), "There should be available agents");
        Agent agent = availableAgents.get(0);

        // Assign the agent to the ticket
        ticketService.assignAgentToTicket(ticketId, agent.getAgentId());

        // Verify a TicketAssignedEvent was sent to Kafka
        ConsumerRecord<String, TicketAssignedEvent> record = assignmentRecords.poll(5, TimeUnit.SECONDS);
        assertNotNull(record, "A TicketAssignedEvent should have been sent");
        assertEquals(ticketId.toString(), record.value().getTicketId());
        assertEquals(agent.getAgentId().toString(), record.value().getAssigneeId());

        // Verify the ticket was updated in the database
        Optional<Ticket> updatedTicket = ticketRepository.findById(ticketId);
        assertTrue(updatedTicket.isPresent());
        assertEquals(agent.getAgentId(), updatedTicket.get().getAssigneeId());
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, updatedTicket.get().getStatus());

        // Verify the agent was updated in the database
        Optional<Agent> updatedAgent = agentRepository.findById(agent.getAgentId());
        assertTrue(updatedAgent.isPresent());
        assertEquals(Agent.AgentAvailability.NOT_AVAILABLE, updatedAgent.get().getAvailability());
        assertEquals(ticketId, updatedAgent.get().getTicketId());
    }

    @Test
    void testMultipleTicketEvents() throws Exception {
        // Create and send multiple ticket events
        for (int i = 0; i < 3; i++) {
            UUID ticketId = UUID.randomUUID();
            NewTicketEvent newTicketEvent = new NewTicketEvent(
                    ticketId.toString(),
                    "user" + i,
                    "OPEN",
                    "Test Subject " + i,
                    "Test Description " + i,
                    "15.03.2025"
            );
            ticketProducer.send(new ProducerRecord<>(KafkaConfig.TICKET_CREATED_TOPIC, newTicketEvent)).get();
        }

        // Wait for the tickets to be processed and saved
        Thread.sleep(3000);

        // Verify all tickets were created
        List<Ticket> tickets = ticketRepository.findAll();
        assertEquals(3, tickets.size(), "All tickets should be saved in the database");

        // Verify ticket details
        for (int j = 0; j < 3; j++) {
            final int index = j;
            assertTrue(tickets.stream().anyMatch(t -> t.getSubject().equals("Test Subject " + index)));
        }
    }

    @Test
    void testInvalidTicketEvent() throws Exception {
        // Create a ticket event with invalid status
        UUID ticketId = UUID.randomUUID();
        NewTicketEvent invalidEvent = new NewTicketEvent(
                ticketId.toString(),
                "user123",
                "INVALID_STATUS", // Invalid status
                "Test Subject",
                "Test Description",
                "15.03.2025"
        );

        // Send the invalid ticket event to Kafka
        ticketProducer.send(new ProducerRecord<>(KafkaConfig.TICKET_CREATED_TOPIC, invalidEvent)).get();

        // Wait for the event to be processed
        Thread.sleep(2000);

        // Verify the ticket was not created due to invalid status
        Optional<Ticket> savedTicket = ticketRepository.findById(ticketId);
        assertFalse(savedTicket.isPresent(), "Ticket should not be saved with invalid status");
    }
}
