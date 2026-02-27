package com.aplication.rest.instruments.config.open_api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApi {
    @Value("${app.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI openAPI() {

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("instruments e-commerce API")
                .version("1.0.0")
                .description("Manage all products, brands, orders")
                .license(mitLicense);

        final String securitySchemeName = "bearerAuth"; // security schema name bearer

        return new OpenAPI()
                .info(info) // applies info to all the API
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // applies security schema, expects a JWT token
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .servers(List.of());
    }
}
