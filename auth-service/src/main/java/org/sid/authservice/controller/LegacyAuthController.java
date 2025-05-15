package org.sid.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour gérer les anciennes routes d'authentification (/api/auth/*)
 * Ce contrôleur est nécessaire pour maintenir la compatibilité avec le frontend
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LegacyAuthController {

    /**
     * Endpoint de login qui retourne toujours un token valide
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody(required = false) Map<String, String> request) {
        System.out.println("=== REQUÊTE LOGIN REÇUE DANS LEGACY AUTH CONTROLLER ===");
        if (request != null && request.containsKey("username")) {
            System.out.println("Username: " + request.get("username"));
        }
        
        // Token JWT statique pour la démonstration
        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImhhbXphIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE2NDYyMzkwMjIsInJvbGVzIjpbIlJPTEVfVVNFUiJdfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        // Format de réponse attendu par le frontend
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", mockToken);
        response.put("token", mockToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", 3600);
        response.put("refresh_token", mockToken);
        response.put("scope", "openid profile email");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test pour vérifier que le contrôleur est accessible
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("LegacyAuthController is working!");
    }
    
    /**
     * Endpoint pour valider un token
     */
    @GetMapping("/validate-auth")
    public ResponseEntity<Boolean> validateAuth() {
        return ResponseEntity.ok(true);
    }
    
    /**
     * Endpoint pour récupérer les informations de l'utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "hamza");
        userInfo.put("email", "hamza@example.com");
        userInfo.put("roles", new String[]{"ROLE_USER"});
        return ResponseEntity.ok(userInfo);
    }
    
    /**
     * Endpoint pour la déconnexion
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Déconnexion réussie");
    }
}
