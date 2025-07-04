package com.sporty.group.ticketmanagementservice.integration;

import com.sporty.group.sportygroupticketingcommons.event.NewTicketEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketAssignedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import com.sporty.group.sportygroupticketingcommons.event.TicketStatusUpdatedEvent;
import com.sporty.group.ticketmanagementservice.config.TestKafkaConfig;
import com.sporty.group.ticketmanagementservice.repository.TicketRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Import(TestKafkaConfig.class)
@DirtiesContext
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected TicketRepository ticketRepository;

    @Value("${spring.kafka.bootstrap-servers}")
    protected String bootstrapServers;

    @Value("${kafka.topic.support-tickets}")
    protected String supportTicketsTopic;

    @Value("${kafka.topic.ticket-assignments}")
    protected String ticketAssignmentsTopic;

    @Value("${kafka.topic.ticket-updates}")
    protected String ticketUpdatesTopic;

    @Value("${kafka.topic.ticket-created}")
    protected String ticketCreatedTopic;

    protected KafkaTemplate<String, TicketCreatedEvent> ticketCreatedKafkaTemplate;
    protected KafkaTemplate<String, TicketAssignedEvent> ticketAssignedKafkaTemplate;
    protected KafkaTemplate<String, TicketStatusUpdatedEvent> ticketStatusUpdatedKafkaTemplate;

    protected BlockingQueue<ConsumerRecord<String, NewTicketEvent>> newTicketEvents;
    protected KafkaMessageListenerContainer<String, NewTicketEvent> newTicketListenerContainer;

    @BeforeEach
    public void setUp() throws Exception {
        // Clear repository before each test
        ticketRepository.deleteAll();

        // Set up Kafka templates for sending events
        ticketCreatedKafkaTemplate = createKafkaTemplate(TicketCreatedEvent.class);
        ticketAssignedKafkaTemplate = createKafkaTemplate(TicketAssignedEvent.class);
        ticketStatusUpdatedKafkaTemplate = createKafkaTemplate(TicketStatusUpdatedEvent.class);

        // Set up consumer for NewTicketEvent
        newTicketEvents = new LinkedBlockingQueue<>();
        newTicketListenerContainer = createNewTicketListenerContainer();
        newTicketListenerContainer.start();

        // Wait until the container has the required number of assigned partitions
        // Add a retry mechanism
        int maxRetries = 5;
        int retryCount = 0;
        boolean assigned = false;

        while (!assigned && retryCount < maxRetries) {
            try {
                ContainerTestUtils.waitForAssignment(newTicketListenerContainer, 1);
                assigned = true;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw e;
                }
                // Wait before retrying
                Thread.sleep(1000);
            }
        }
    }

    private <T> KafkaTemplate<String, T> createKafkaTemplate(Class<T> eventType) {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, T> producerFactory = 
                new DefaultKafkaProducerFactory<>(producerProps);

        return new KafkaTemplate<>(producerFactory);
    }

    private KafkaMessageListenerContainer<String, NewTicketEvent> createNewTicketListenerContainer() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "integration-test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.sporty.group.ticketmanagementservice.model.event");

        DefaultKafkaConsumerFactory<String, NewTicketEvent> consumerFactory = 
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        new JsonDeserializer<>(NewTicketEvent.class, false));

        ContainerProperties containerProperties = new ContainerProperties(ticketCreatedTopic);
        KafkaMessageListenerContainer<String, NewTicketEvent> container = 
                new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        container.setupMessageListener((MessageListener<String, NewTicketEvent>) newTicketEvents::add);

        return container;
    }

    protected NewTicketEvent receiveNewTicketEvent(long timeoutSeconds) throws InterruptedException {
        ConsumerRecord<String, NewTicketEvent> record = newTicketEvents.poll(timeoutSeconds, TimeUnit.SECONDS);
        return record != null ? record.value() : null;
    }
}
