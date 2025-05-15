package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuration d'urgence pour désactiver complètement la sécurité sur /api/auth/login
 * Cette config a la priorité la plus haute (Order = -100) pour s'assurer qu'elle s'applique avant les autres
 */
@Configuration
@EnableWebSecurity
@Order(-100) // Priorité très haute pour s'exécuter avant toute autre config
public class EmergencySecurityConfig {

    @Bean
    public SecurityFilterChain emergencyFilterChain(HttpSecurity http) throws Exception {
        System.out.println("=== APPLYING EMERGENCY SECURITY CONFIG ===");
        
        // Désactive complètement la sécurité pour tous les endpoints d'authentification
        http
            .securityMatcher("/api/auth/**", "/api/test-auth/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .anonymous(anonymous -> anonymous.disable())
            .oauth2ResourceServer(oauth2 -> oauth2.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());
        
        return http.build();
    }
}
