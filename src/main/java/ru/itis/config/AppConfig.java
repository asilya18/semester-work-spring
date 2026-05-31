package ru.itis.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableCaching // включает механизм кэширования спринг
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatRelaxedChars() {
        // разрешаем кириллицу в query string (браузеры иногда не кодируют)
        return factory -> factory.addConnectorCustomizers(connector ->
                connector.setProperty("relaxedQueryChars",
                        "Ѐ-ӿабвгдеёжзий" +
                        "клмнопрстуфхц" +
                        "чшщъыьэюя" +
                        "АБВГДЕЁЖЗИЙ" +
                        "КЛМНОПРСТУФХЦ" +
                        "ЧШЩЪЫЬЭЮЯ")
        );
    }
}