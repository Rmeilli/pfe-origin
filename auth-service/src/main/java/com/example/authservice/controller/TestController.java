package com.example.authservice.controller;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", maxAge = 3600)
public class TestController {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @GetMapping("/public")
    public String publicTest() {
        return "Public endpoint is working!";
    }

    @PostMapping("/public")
    public String publicPostTest() {
        return "Public POST endpoint is working!";
    }
    
    @GetMapping("/keycloak-status")
    public Map<String, Object> keycloakStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("server_url", serverUrl);
        status.put("realm", realm);
        status.put("client_id", clientId);
        status.put("client_secret_configured", clientSecret != null && !clientSecret.isEmpty());
        
        try {
            // Test de connexion à Keycloak
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm("master")
                    .clientId("admin-cli")
                    .username("admin")
                    .password("admin")
                    .build();
            
            // Récupérer une liste de realms pour vérifier la connexion
            keycloak.realms().findAll();
            status.put("keycloak_connection", "OK");
            
            // Vérifier si le realm existe
            boolean realmExists = keycloak.realms().findAll().stream()
                    .anyMatch(r -> r.getRealm().equals(realm));
            status.put("realm_exists", realmExists);
            
            if (realmExists) {
                // Vérifier si le client existe
                boolean clientExists = keycloak.realm(realm).clients().findAll().stream()
                        .anyMatch(c -> c.getClientId().equals(clientId));
                status.put("client_exists", clientExists);
                
                // Vérifier si le client est activé
                if (clientExists) {
                    boolean clientEnabled = keycloak.realm(realm).clients().findAll().stream()
                            .filter(c -> c.getClientId().equals(clientId))
                            .findFirst()
                            .map(c -> c.isEnabled())
                            .orElse(false);
                    status.put("client_enabled", clientEnabled);
                }
            }
        } catch (Exception e) {
            status.put("keycloak_connection", "ERROR");
            status.put("error_message", e.getMessage());
        }
        
        return status;
    }
} 