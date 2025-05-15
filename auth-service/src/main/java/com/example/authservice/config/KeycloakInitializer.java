package com.example.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class KeycloakInitializer implements ApplicationRunner {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=========== KEYCLOAK CONFIGURATION ===========");
        System.out.println("Keycloak Server URL: " + serverUrl);
        System.out.println("Realm: " + realm);
        System.out.println("Client ID: " + clientId);
        System.out.println("Client Secret: " + (clientSecret != null && !clientSecret.isEmpty() ? "[CONFIGURED]" : "[NOT CONFIGURED]"));
        System.out.println("============================================");
        
        System.out.println("IMPORTANT: Assurez-vous que le client " + clientId + " est configuré dans Keycloak");
        System.out.println("avec les paramètres suivants:");
        System.out.println("- Access Type: confidential");
        System.out.println("- Service Accounts Enabled: ON");
        System.out.println("- Authorization Enabled: ON");
        System.out.println("- Valid Redirect URIs: http://localhost:8081/*");
        System.out.println("- Web Origins: +");
        System.out.println("============================================");
    }
} 