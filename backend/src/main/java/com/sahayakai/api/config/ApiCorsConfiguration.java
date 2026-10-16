package com.sahayakai.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class ApiCorsConfiguration implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public ApiCorsConfiguration(
            @Value("${sahayak.cors.allowed-origin:http://localhost:5173,http://127.0.0.1:5173}") String allowedOrigin) {
        this.allowedOrigins = Arrays.stream(allowedOrigin.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        if (allowedOrigins.length == 0 || Arrays.asList(allowedOrigins).contains("*")) {
            throw new IllegalArgumentException("Configure one or more exact CORS origins; wildcard origins are not allowed.");
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PATCH", "OPTIONS")
                .allowedHeaders("Accept", "Authorization", "Content-Type")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
