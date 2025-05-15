package com.example.authservice.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Cette classe désactive l'auto-configuration OAuth2 Resource Server
 * qui force la validation JWT sur toutes les routes, y compris /api/auth/login
 */
@Configuration
@EnableAutoConfiguration(exclude = {
    OAuth2ResourceServerAutoConfiguration.class
})
public class DisableOAuth2ResourceServerAutoConfig {
    // Cette classe est vide, son seul but est de désactiver l'auto-configuration OAuth2 Resource Server
}
