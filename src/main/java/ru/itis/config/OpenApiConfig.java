package ru.itis.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MovieNight API",
        version = "1.0",
        description = "REST API для приложения совместного просмотра фильмов",
        contact = @Contact(name = "MovieNight", email = "admin@mail.ru")
    )
)
public class OpenApiConfig {
}