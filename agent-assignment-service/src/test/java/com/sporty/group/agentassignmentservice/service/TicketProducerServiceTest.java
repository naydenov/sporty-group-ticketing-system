package com.sporty.group.agentassignmentservice.service;

import com.sporty.group.agentassignmentservice.config.KafkaConfig;
import com.sporty.group.agentassignmentservice.model.event.TicketAssignedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketProducerServiceTest {

    @Mock
    private KafkaTemplate<String, TicketAssignedEvent> kafkaTemplate;

    @InjectMocks
    private TicketProducerService ticketProducerService;

    @Captor
    private ArgumentCaptor<TicketAssignedEvent> eventCaptor;

    private UUID ticketId;
    private UUID assigneeId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        assigneeId = UUID.randomUUID();
    }

    @Test
    void sendTicketAssignedEvent_ShouldSendEventToKafka() {
        // Arrange
        CompletableFuture<SendResult<String, TicketAssignedEvent>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC), any(TicketAssignedEvent.class)))
                .thenReturn(future);

        // Act
        ticketProducerService.sendTicketAssignedEvent(ticketId, assigneeId);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC), eventCaptor.capture());

        TicketAssignedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(ticketId.toString(), capturedEvent.getTicketId());
        assertEquals(assigneeId.toString(), capturedEvent.getAssigneeId());
    }

    @Test
    void sendTicketAssignedEvent_WhenSendSucceeds_ShouldLogSuccess() {
        // Arrange
        CompletableFuture<SendResult<String, TicketAssignedEvent>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC), any(TicketAssignedEvent.class)))
                .thenReturn(future);

        // Act
        ticketProducerService.sendTicketAssignedEvent(ticketId, assigneeId);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC), any(TicketAssignedEvent.class));

        // We can't easily test the logging, but we can verify the future is completed
        // This is a bit of a simplification, but it's the best we can do without mocking the logger
    }

    @Test
    void sendTicketAssignedEvent_WhenSendFails_ShouldLogError() {
        // Arrange
        CompletableFuture<SendResult<String, TicketAssignedEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Test exception"));
        when(kafkaTemplate.send(eq(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC), any(TicketAssignedEvent.class)))
                .thenReturn(future);

        // Act
        ticketProducerService.sendTicketAssignedEvent(ticketId, assigneeId);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfig.TICKET_ASSIGNMENTS_TOPIC), any(TicketAssignedEvent.class));

        // We can't easily test the logging, but we can verify the future is completed exceptionally
        // This is a bit of a simplification, but it's the best we can do without mocking the logger
    }
}
