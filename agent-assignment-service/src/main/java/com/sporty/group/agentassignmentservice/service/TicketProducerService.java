package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.config.KafkaConfig;
import com.sporty.group.agentassignmentservice.model.event.TicketAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketProducerService {

    private final KafkaTemplate<String, TicketAssignedEvent> kafkaTemplate;

    public void sendTicketAssignedEvent(UUID ticketId, UUID assigneeId) {
        TicketAssignedEvent event = new TicketAssignedEvent(
                ticketId.toString(),
                assigneeId.toString()
        );
        
        log.info("Sending ticket assigned event: {}", event);
        
        kafkaTemplate.send(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent message=[{}] with offset=[{}]",
                                event,
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Unable to send message=[{}] due to : {}", event, ex.getMessage(), ex);
                    }
                });
    }
}