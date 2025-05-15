package org.sid.authservice1.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.sid.authservice1.model.AuthRequest;
import org.sid.authservice1.model.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);
    
    private final Keycloak keycloakAdmin;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public KeycloakService(Keycloak keycloakAdmin) {
        this.keycloakAdmin = keycloakAdmin;
    }

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    /**
     * Authentifie un utilisateur auprès de Keycloak
     * @param request Requête d'authentification contenant username et password
     * @return Réponse d'authentification contenant le token
     */
    public AuthResponse login(AuthRequest request) {
        log.info("Tentative de connexion pour l'utilisateur: {}", request.getUsername());
        
        try {
            String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("username", request.getUsername());
            map.add("password", request.getPassword());
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
            
            log.debug("Envoi de la requête d'authentification à Keycloak: {}", tokenUrl);
            ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    AccessTokenResponse.class
            );
            
            log.info("Authentification réussie pour l'utilisateur: {}", request.getUsername());
            AccessTokenResponse tokenResponse = response.getBody();
            
            return AuthResponse.builder()
                    .access_token(tokenResponse.getToken())
                    .refresh_token(tokenResponse.getRefreshToken())
                    .expires_in(tokenResponse.getExpiresIn())
                    .token_type("Bearer")
                    .build();
            
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification pour l'utilisateur {}: {}", 
                    request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Échec de l'authentification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valide un token JWT
     * @param token Token à valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            String introspectUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token/introspect";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("token", token);
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    introspectUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            Map<String, Object> introspectionResponse = response.getBody();
            return introspectionResponse != null && (boolean) introspectionResponse.getOrDefault("active", false);
            
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Récupère les informations de l'utilisateur à partir du token
     * @param token Token d'authentification
     * @return Map contenant les informations de l'utilisateur
     */
    public Map<String, Object> getUserInfo(String token) {
        try {
            String userInfoUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations utilisateur: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Impossible de récupérer les informations utilisateur");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }
    
    /**
     * Déconnecte un utilisateur en invalidant son token
     * @param token Token à invalider
     * @return true si la déconnexion a réussi, false sinon
     */
    public boolean logout(String token, String refreshToken) {
        try {
            String logoutUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("refresh_token", refreshToken);
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
            
            restTemplate.exchange(
                    logoutUrl,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
            
            return true;
            
        } catch (Exception e) {
            log.error("Erreur lors de la déconnexion: {}", e.getMessage(), e);
            return false;
        }
    }
}
