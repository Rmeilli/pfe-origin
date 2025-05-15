package com.example.authservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur de test pour diagnostiquer les problèmes de connexion Keycloak
 */
@RestController
@RequestMapping("/api/test")
public class TestKeycloakController {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Endpoint de test pour vérifier la connexion à Keycloak
     */
    @GetMapping("/keycloak-connection")
    public ResponseEntity<?> testKeycloakConnection(
            @RequestParam(defaultValue = "hamza") String username,
            @RequestParam(defaultValue = "test123") String password) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("serverUrl", serverUrl);
        result.put("realm", realm);
        result.put("clientId", clientId);
        result.put("clientSecret", clientSecret != null ? clientSecret.substring(0, 5) + "..." : "null");
        
        try {
            // Test de connexion à Keycloak
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("username", username);
            formData.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            result.put("tokenUrl", tokenUrl);
            
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        tokenUrl,
                        HttpMethod.POST,
                        request,
                        String.class
                );
                
                result.put("status", response.getStatusCode().toString());
                result.put("response", response.getBody());
                return ResponseEntity.ok(result);
                
            } catch (Exception e) {
                result.put("error", e.getMessage());
                result.put("errorType", e.getClass().getName());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
            
        } catch (Exception e) {
            result.put("error", "Erreur lors du test de connexion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
