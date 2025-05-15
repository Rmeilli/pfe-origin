package com.example.authservice.controller;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.service.SimpleKeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur d'authentification simplifié
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SimpleAuthController {

    private final SimpleKeycloakService keycloakService;

    @Autowired
    public SimpleAuthController(SimpleKeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    /**
     * Endpoint de login simplifié
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        System.out.println("=== REQUÊTE LOGIN REÇUE ===");
        System.out.println("Username: " + request.getUsername());
        
        AuthResponse response = keycloakService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test pour vérifier que le contrôleur est accessible
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("SimpleAuthController is working!");
    }
}
