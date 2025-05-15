package org.sid.authservice1.controller;

import org.sid.authservice1.model.AuthRequest;
import org.sid.authservice1.model.AuthResponse;
import org.sid.authservice1.service.KeycloakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final KeycloakService keycloakService;
    
    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    /**
     * Endpoint de login qui authentifie l'utilisateur avec Keycloak
     * @param request Requête d'authentification contenant username et password
     * @return Réponse contenant le token d'accès
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        log.info("Requête de login reçue pour l'utilisateur: {}", request.getUsername());
        try {
            AuthResponse response = keycloakService.login(request);
            log.info("Login réussi pour l'utilisateur: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors du login pour l'utilisateur {}: {}", request.getUsername(), e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Échec de l'authentification");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint pour valider un token d'authentification
     * @param token Token à valider
     * @return true si le token est valide, false sinon
     */
    @GetMapping("/validate-auth")
    public ResponseEntity<Map<String, Object>> validateAuth(@RequestHeader("Authorization") String authHeader) {
        log.info("Requête de validation de token reçue");
        
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("Token non fourni", HttpStatus.BAD_REQUEST);
        }
        
        boolean isValid = keycloakService.validateToken(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        
        if (isValid) {
            log.info("Token validé avec succès");
            return ResponseEntity.ok(response);
        } else {
            log.warn("Token invalide");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Endpoint pour récupérer les informations de l'utilisateur
     * @param authHeader En-tête d'autorisation contenant le token
     * @return Informations de l'utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        log.info("Requête d'informations utilisateur reçue");
        
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("Token non fourni", HttpStatus.BAD_REQUEST);
        }
        
        boolean isValid = keycloakService.validateToken(token);
        if (!isValid) {
            return createErrorResponse("Token invalide", HttpStatus.UNAUTHORIZED);
        }
        
        Map<String, Object> userInfo = keycloakService.getUserInfo(token);
        log.info("Informations utilisateur récupérées avec succès");
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Endpoint de déconnexion
     * @param authHeader En-tête d'autorisation contenant le token
     * @param refreshToken Token de rafraîchissement
     * @return Message de succès ou d'erreur
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) Map<String, String> body) {
        
        log.info("Requête de déconnexion reçue");
        
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("Token non fourni", HttpStatus.BAD_REQUEST);
        }
        
        String refreshToken = body != null ? body.get("refresh_token") : null;
        
        boolean success = keycloakService.logout(token, refreshToken);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("message", "Déconnexion réussie");
            log.info("Déconnexion réussie");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Échec de la déconnexion");
            log.warn("Échec de la déconnexion");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Extrait le token de l'en-tête d'autorisation
     * @param authHeader En-tête d'autorisation
     * @return Token extrait ou null si l'en-tête est invalide
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Crée une réponse d'erreur
     * @param message Message d'erreur
     * @param status Code de statut HTTP
     * @return ResponseEntity contenant l'erreur
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
