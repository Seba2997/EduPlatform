package com.eduplatform.apiReporte.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiDoc() {
        return new OpenAPI()
            .info(new Info()
                .title("API EduPlatform - Reportes")
                .description("Gestion de reportes con autenticación JWT")
                .version("1.0.0")
            )
            .components(new Components()
                .addSecuritySchemes("bearer-key",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            .addSecurityItem(
                new SecurityRequirement().addList("bearer-key", List.of())
            );
    }
}