package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        System.out.println("=== APPLYING GLOBAL CORS CONFIGURATION ===");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Autoriser les credentials
        config.setAllowCredentials(true);
        
        // Autoriser toutes les origines ou spécifier l'origine du frontend
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Autoriser tous les headers
        config.setAllowedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization",
            "X-Requested-With", "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        
        // Autoriser toutes les méthodes
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Exposer les headers
        config.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
            "Authorization"
        ));
        
        // Durée de mise en cache des résultats de pre-flight
        config.setMaxAge(3600L);
        
        // Appliquer cette configuration à tous les chemins
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
