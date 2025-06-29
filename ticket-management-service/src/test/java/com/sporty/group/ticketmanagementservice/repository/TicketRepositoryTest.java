package com.sporty.group.ticketmanagementservice.repository;

import com.sporty.group.ticketmanagementservice.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Ticket testTicket;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        testTicket = Ticket.builder()
                .ticketId(ticketId)
                .subject("Test Subject")
                .description("Test Description")
                .status(Ticket.TicketStatus.OPEN)
                .userId("user-001")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void save_shouldSaveTicket() {
        // When
        Ticket savedTicket = ticketRepository.save(testTicket);

        // Then
        assertEquals(testTicket.getTicketId(), savedTicket.getTicketId());
        assertEquals(testTicket.getSubject(), savedTicket.getSubject());
        assertEquals(testTicket.getDescription(), savedTicket.getDescription());
        assertTrue(ticketRepository.existsById(ticketId));
    }

    @Test
    void findById_shouldReturnTicket_whenTicketExists() {
        // Given
        entityManager.persist(testTicket);
        entityManager.flush();

        // When
        Optional<Ticket> foundTicket = ticketRepository.findById(ticketId);

        // Then
        assertTrue(foundTicket.isPresent());
        assertEquals(testTicket.getTicketId(), foundTicket.get().getTicketId());
        assertEquals(testTicket.getSubject(), foundTicket.get().getSubject());
        assertEquals(testTicket.getDescription(), foundTicket.get().getDescription());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenTicketDoesNotExist() {
        // When
        Optional<Ticket> foundTicket = ticketRepository.findById(UUID.randomUUID());

        // Then
        assertFalse(foundTicket.isPresent());
    }

    @Test
    void existsById_shouldReturnTrue_whenTicketExists() {
        // Given
        entityManager.persist(testTicket);
        entityManager.flush();

        // When
        boolean exists = ticketRepository.existsById(ticketId);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenTicketDoesNotExist() {
        // When
        boolean exists = ticketRepository.existsById(UUID.randomUUID());

        // Then
        assertFalse(exists);
    }
}
