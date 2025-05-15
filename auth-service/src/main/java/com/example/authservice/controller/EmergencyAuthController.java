package com.example.authservice.controller;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur d'urgence pour l'authentification
 * Fournit un endpoint simple qui retourne toujours un token valide
 * Utilisé pour débloquer le développement en cas de problèmes avec Keycloak
 */
@RestController
@RequestMapping("/api/emergency")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class EmergencyAuthController {

    /**
     * Endpoint d'urgence qui retourne toujours un token valide
     */
    @PostMapping("/login")
    public ResponseEntity<?> emergencyLogin(@RequestBody AuthRequest request) {
        System.out.println("=== EMERGENCY LOGIN ENDPOINT CALLED ===");
        System.out.println("Username: " + request.getUsername());
        
        // Token JWT statique pour les tests
        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImhhbXphIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE2NDYyMzkwMjIsInJvbGVzIjpbIlJPTEVfVVNFUiJdfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        System.out.println("=== EMERGENCY TOKEN GENERATED ===");
        
        return ResponseEntity.ok(new AuthResponse(mockToken));
    }
    
    /**
     * Endpoint de test pour vérifier que le contrôleur est accessible
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Emergency auth controller is working!");
    }
}
