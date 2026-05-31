package ru.itis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.itis.converter.MovieNightToFormConverter;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final MovieNightToFormConverter movieNightToFormConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(movieNightToFormConverter);
    }
}