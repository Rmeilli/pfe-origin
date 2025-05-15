package com.example.authservice.service;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Keycloak getKeycloakInstance() {
        // Utilisez l'interface admin pour les opérations administratives
        System.out.println("Création d'une instance Keycloak avec URL: " + serverUrl);
        
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")  // Le realm master est toujours utilisé pour l'administration
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();
    }

    public String createUser(String username, String password) {
        System.out.println("Tentative de création d'utilisateur: " + username);
        
        try {
            // 1. Obtenir l'instance Keycloak
            Keycloak keycloak = getKeycloakInstance();
            
            // 2. Configurer l'utilisateur
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(username);
            
            // 3. Configurer le mot de passe
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));
            
            // 4. Créer l'utilisateur
            Response response = keycloak.realm(realm).users().create(user);
            int status = response.getStatus();
            
            System.out.println("Réponse de création utilisateur: " + status);
            
            if (status >= 200 && status < 300) {
                // L'utilisateur a été créé avec succès
                String userId = extractUserId(response);
                System.out.println("Utilisateur créé avec ID: " + userId);
                
                // 5. Assigner le rôle "user"
                try {
                    assignRoleToUser(keycloak, userId, "ROLE_USER");
                } catch (Exception e) {
                    System.out.println("Erreur lors de l'assignation du rôle ROLE_USER: " + e.getMessage());
                    try {
                        assignRoleToUser(keycloak, userId, "user");
                    } catch (Exception e2) {
                        System.out.println("Erreur lors de l'assignation du rôle user: " + e2.getMessage());
                    }
                }
                
                return userId;
            } else {
                System.out.println("Erreur lors de la création de l'utilisateur: " + status);
                String responseBody = response.readEntity(String.class);
                System.out.println("Détail de l'erreur: " + responseBody);
                throw new RuntimeException("Erreur lors de la création de l'utilisateur: " + responseBody);
            }
        } catch (Exception e) {
            System.out.println("Exception lors de la création de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de l'utilisateur", e);
        }
    }
    
    private String extractUserId(Response response) {
        String locationHeader = response.getHeaderString("Location");
        if (locationHeader != null) {
            return locationHeader.replaceAll(".*/([^/]+)$", "$1");
        }
        throw new RuntimeException("ID utilisateur introuvable dans la réponse");
    }
    
    private void assignRoleToUser(Keycloak keycloak, String userId, String roleName) {
        try {
            // 1. Obtenir le rôle
            var roleRepresentation = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            
            // 2. Assigner le rôle à l'utilisateur
            keycloak.realm(realm).users().get(userId).roles()
                    .realmLevel().add(Collections.singletonList(roleRepresentation));
            
            System.out.println("Rôle " + roleName + " assigné avec succès à l'utilisateur " + userId);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'assignation du rôle " + roleName + ": " + e.getMessage());
            throw e;
        }
    }

    public List<UserRepresentation> getUsers() {
        try {
            Keycloak keycloak = getKeycloakInstance();
            return keycloak.realm(realm).users().list();
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs", e);
        }
    }

    public void deleteUser(String userId) {
        try {
            Keycloak keycloak = getKeycloakInstance();
            keycloak.realm(realm).users().delete(userId);
            System.out.println("Utilisateur " + userId + " supprimé avec succès");
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }
    
    // Getters pour les propriétés Keycloak
    public String getServerUrl() {
        return serverUrl;
    }
    
    public String getRealm() {
        return realm;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }

    public AuthResponse login(AuthRequest authRequest) {
        System.out.println("=== TENTATIVE DE CONNEXION KEYCLOAK ===");
        System.out.println("Utilisateur: " + authRequest.getUsername());
        System.out.println("URL Keycloak: " + serverUrl);
        System.out.println("Realm: " + realm);
        System.out.println("Client ID: " + clientId);
        
        try {
            // Utilisation de MultiValueMap pour une meilleure gestion des paramètres
            String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            System.out.println("Token URL: " + tokenUrl);
            
            // Configuration des en-têtes
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // Construction du corps de la requête avec MultiValueMap
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("username", authRequest.getUsername());
            formData.add("password", authRequest.getPassword());
            
            // Création de l'entité HTTP
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            
            System.out.println("Envoi de la requête à Keycloak...");
            
            // Exécution de la requête avec postForEntity pour plus de simplicité
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    tokenUrl,
                    request,
                    Map.class
            );
            
            System.out.println("Réponse reçue avec statut: " + response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                System.out.println("Contenu de la réponse: " + responseBody.keySet());
                
                String accessToken = (String) responseBody.get("access_token");
                
                if (accessToken != null) {
                    System.out.println("Token généré avec succès (longueur: " + accessToken.length() + ")");
                    return new AuthResponse(accessToken);
                } else {
                    throw new RuntimeException("Token absent de la réponse");
                }
            } else {
                throw new RuntimeException("Échec de connexion: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("=== ERREUR HTTP CLIENT ===");
            System.out.println("Statut: " + e.getStatusCode());
            System.out.println("Message: " + e.getResponseBodyAsString());
            e.printStackTrace();
            throw new RuntimeException("Erreur d'authentification Keycloak: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            System.out.println("=== ERREUR GÉNÉRALE ===");
            System.out.println("Type: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token) {
        try {
            // Log token prefix for debugging (don't log the entire token for security)
            System.out.println("Validating token starting with: " + token.substring(0, Math.min(10, token.length())) + "...");
            
            // Set up the Keycloak configuration
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("token", token);
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
    
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
    
            // Call Keycloak's introspect endpoint
            String introspectUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token/introspect";
            System.out.println("Calling Keycloak introspect endpoint: " + introspectUrl);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(introspectUrl, request, Map.class);
            System.out.println("Keycloak response status: " + response.getStatusCode());
    
            // Check if token is active
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                System.out.println("Keycloak response body: " + responseBody);
                return Boolean.TRUE.equals(responseBody.get("active"));
            } else {
                System.out.println("Keycloak response body is null");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error validating token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public String getUsernameFromToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("token", token);
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
    
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
    
            String introspectUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token/introspect";
            ResponseEntity<Map> response = restTemplate.postForEntity(introspectUrl, request, Map.class);
    
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && Boolean.TRUE.equals(responseBody.get("active"))) {
                // Try to get preferred_username first, then username, then sub as fallback
                if (responseBody.containsKey("preferred_username")) {
                    return (String) responseBody.get("preferred_username");
                } else if (responseBody.containsKey("username")) {
                    return (String) responseBody.get("username");
                } else {
                    return (String) responseBody.get("sub");
                }
            } else {
                throw new RuntimeException("Token is not active or response is null");
            }
        } catch (Exception e) {
            System.err.println("Error extracting username from token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error extracting username from token", e);
        }
    }
    
    public void logoutUser(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("token", token);
    
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            String logoutUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
            
            System.out.println("Calling Keycloak logout endpoint: " + logoutUrl);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                    logoutUrl,
                    request,
                    String.class
            );
            
            System.out.println("Logout response: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error during logout", e);
        }
    }
}