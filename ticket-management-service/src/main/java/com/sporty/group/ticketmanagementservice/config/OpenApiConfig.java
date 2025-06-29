package com.sporty.group.ticketmanagementservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    @Value("${springdoc.server.url:http://localhost:8080}")
    private String serverUrl;

    /**
     * Configures the OpenAPI documentation for the application.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI ticketManagementOpenAPI() {
        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription("Server URL");

        Contact contact = new Contact();
        contact.setName("Sporty Group");
        contact.setEmail("support@sportygroup.com");
        contact.setUrl("https://sportygroup.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("Ticket Management API")
                .version("1.0.0")
                .description("API for managing support tickets")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}