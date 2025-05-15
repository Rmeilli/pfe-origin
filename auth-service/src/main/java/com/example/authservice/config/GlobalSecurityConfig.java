package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration globale de sécurité qui désactive CSRF pour toute l'application
 * Cette configuration a la priorité la plus haute pour s'assurer qu'elle s'applique avant les autres
 */
@Configuration
@EnableWebSecurity
@Order(-1000) // Priorité très haute pour s'exécuter avant toute autre config
public class GlobalSecurityConfig {

    @Bean
    public SecurityFilterChain globalSecurityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("=== APPLYING GLOBAL SECURITY CONFIG - DISABLING CSRF FOR ALL REQUESTS ===");
        
        // Désactive CSRF pour toutes les requêtes
        http
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}
