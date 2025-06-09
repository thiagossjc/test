package org.inditex.prices.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation setup.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures and returns the OpenAPI specification for the application.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inditex Price API")
                        .version("1.0.0")
                        .description("API for retrieving applicable prices for Inditex products"));
    }

    /**
     * Configures a grouped OpenAPI definition for admin endpoints.
     *
     * @return configured GroupedOpenApi instance
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("test")
                .pathsToMatch("/api/prices","/api/prices/filter")
                .build();
    }
}