package com.fidpay.moneytransfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Global CORS Configuration
 * 
 * Allows all API endpoints to accept requests from the Angular frontend.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from Angular dev server
        config.addAllowedOrigin("http://localhost:4200");

        // Allow all common HTTP methods
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");

        // Allow all headers including Authorization
        config.addAllowedHeader("*");

        // Allow credentials (for Basic Auth)
        config.setAllowCredentials(true);

        // Apply to all API paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
