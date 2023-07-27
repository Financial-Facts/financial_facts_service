package com.facts.financial_facts_service.configurations;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@SecurityScheme(
        name = "basicScheme",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Financial Facts Service")
                        .description("Your API Description")
                        .version("1.0"));
    }
}
