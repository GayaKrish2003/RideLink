package com.ridelink.accountservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This just sets the title/description shown at the top of Swagger UI —
// purely cosmetic, but makes the docs look intentional rather than default.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink - Account Service API")
                        .description("Handles passenger/driver registration, login, and profile management")
                        .version("1.0"));
    }
}