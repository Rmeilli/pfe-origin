package org.sid.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur d'authentification ultra simplifié
 */
@RestController
@RequestMapping("/api/direct")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DirectAuthController {

    /**
     * Endpoint de login simplifié
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody(required = false) Map<String, String> request) {
        System.out.println("=== REQUÊTE LOGIN REÇUE DANS LE BON PACKAGE ===");
        
        // Token JWT statique pour la démonstration
        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImhhbXphIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE2NDYyMzkwMjIsInJvbGVzIjpbIlJPTEVfVVNFUiJdfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        // Format de réponse simplifié
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", mockToken);
        response.put("token", mockToken);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test simplifié
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test OK");
    }
    
    /**
     * Endpoint de secours qui ne fait rien
     */
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("DirectAuthController home");
    }
    
    /**
     * Endpoint pour récupérer les informations de l'utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        System.out.println("=== REQUÊTE ME REÇUE DANS DIRECT AUTH CONTROLLER ===");
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "hamza");
        userInfo.put("email", "hamza@example.com");
        userInfo.put("roles", new String[]{"ROLE_USER"});
        return ResponseEntity.ok(userInfo);
    }
}
