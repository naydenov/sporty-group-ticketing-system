package com.sporty.group.ticketapigatewayservice.config;


import com.sporty.group.sportygroupticketingcommons.event.TicketCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaProducerConfigTest {

    @Test
    public void testProducerFactory() {
        // Create the config with a test bootstrap server
        KafkaProducerConfig config = new KafkaProducerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");

        // Get the producer factory
        ProducerFactory<String, TicketCreatedEvent> factory = config.producerFactory();

        // Verify it's the correct type
        assertTrue(factory instanceof DefaultKafkaProducerFactory);

        // Get the configs using reflection (since they're private in DefaultKafkaProducerFactory)
        Map<String, Object> configs = (Map<String, Object>) ReflectionTestUtils.getField(factory, "configs");

        // Verify the configs
        assertNotNull(configs);
        assertEquals("localhost:9092", configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JsonSerializer.class, configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    public void testKafkaTemplate() {
        // Create the config with a test bootstrap server
        KafkaProducerConfig config = new KafkaProducerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");

        // Get the kafka template
        KafkaTemplate<String, TicketCreatedEvent> template = config.kafkaTemplate();

        // Verify it's not null
        assertNotNull(template);

        // Verify it has the correct producer factory
        ProducerFactory<String, TicketCreatedEvent> factory = template.getProducerFactory();
        assertTrue(factory instanceof DefaultKafkaProducerFactory);
    }
}