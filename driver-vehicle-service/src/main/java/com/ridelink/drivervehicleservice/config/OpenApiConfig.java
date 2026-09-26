package com.ridelink.drivervehicleservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driverVehicleServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Driver and Vehicle Service API")
                        .description(
                                "REST API documentation for managing drivers, "
                                        + "vehicles, availability, service areas, "
                                        + "and eligible driver retrieval.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RideLink Team")));
    }
}