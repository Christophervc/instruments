package com.aplication.rest.instruments.config.open_api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApi {
    @Value("${app.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("instruments e-commerce API")
                .version("1.0.0")

                .description("Manage all products, brands, orders")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of());
    }
}
