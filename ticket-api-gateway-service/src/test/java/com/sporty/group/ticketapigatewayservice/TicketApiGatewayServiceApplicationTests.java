package com.sporty.group.ticketapigatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({TestcontainersConfiguration.class})
@SpringBootTest
class TicketApiGatewayServiceApplicationTests {
    
    @Test
    void contextLoads() {
        // This test verifies that the application context loads successfully
    }
}