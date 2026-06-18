package com.example.carrental;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS-Konfiguration.
 * Erlaubt dem React-Frontend (anderer Port) den Zugriff auf die REST-API.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Gibt die API gezielt fuer die Frontend-Adressen und die genutzten
     * HTTP-Methoden frei.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
