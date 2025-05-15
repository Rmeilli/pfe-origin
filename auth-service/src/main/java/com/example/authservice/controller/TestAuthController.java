package com.example.authservice.controller;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-auth")
@CrossOrigin(origins = "*")
public class TestAuthController {

    /**
     * Endpoint de test qui retourne toujours un token valide pour les tests
     */
    @PostMapping("/login")
    public ResponseEntity<?> testLogin(@RequestBody AuthRequest request) {
        System.out.println("=== REQUÊTE TEST LOGIN REÇUE ===");
        System.out.println("Username: " + request.getUsername());
        
        // Token JWT statique pour les tests
        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImhhbXphIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE2NDYyMzkwMjIsInJvbGVzIjpbIlJPTEVfVVNFUiJdfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        System.out.println("=== TOKEN TEST GÉNÉRÉ ===");
        
        return ResponseEntity.ok(new AuthResponse(mockToken));
    }
}
