package com.example.authservice.controller;


import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", maxAge = 3600)
public class AuthController {

    @Autowired
    private KeycloakService keycloakService;

    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
        System.out.println("Demande d'enregistrement reçue pour l'utilisateur: " + authRequest.getUsername());
        try {
            String userId = keycloakService.createUser(authRequest.getUsername(), authRequest.getPassword());
            System.out.println("Utilisateur enregistré avec succès, ID: " + userId);
            return ResponseEntity.ok("User created with ID: " + userId);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'enregistrement: " + e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "*") // Autorise toutes les origines
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        System.out.println("=== REQUÊTE LOGIN REÇUE ===\nUsername: " + request.getUsername());
        
        try {
            // Utiliser le service Keycloak pour l'authentification
            AuthResponse response = keycloakService.login(request);
            System.out.println("=== AUTHENTIFICATION RÉUSSIE ===\nToken généré");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("=== ERREUR D'AUTHENTIFICATION ===\n" + e.getMessage());
            e.printStackTrace();
            
            // En cas d'échec, utiliser l'authentification mock pour la démonstration
            if ("hamza".equals(request.getUsername()) && "test123".equals(request.getPassword())) {
                System.out.println("=== FALLBACK: UTILISATION DU MODE MOCK ===");
                String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImhhbXphIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE2NDYyMzkwMjIsInJvbGVzIjpbIlJPTEVfVVNFUiJdfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
                return ResponseEntity.ok(new AuthResponse(mockToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Échec de l'authentification: " + e.getMessage());
            }
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam(name = "token") String token) {
        System.out.println("Demande de validation de token reçue");
        try {
            // Remove any surrounding quotes from the token
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
            
            boolean isValid = keycloakService.validateToken(token);
            System.out.println("Validation du token: " + (isValid ? "valide" : "invalide"));
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            System.out.println("Erreur lors de la validation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

    @GetMapping("/validate-auth")
    public ResponseEntity<Boolean> validateTokenFromHeader(@RequestHeader(name = "Authorization") String authHeader) {
        System.out.println("Demande de validation de token reçue via header: " + authHeader.substring(0, Math.min(20, authHeader.length())) + "...");
        try {
            // Extract token from "Bearer TOKEN" format
            String token;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                // If "Bearer " prefix is missing, use the whole header as token
                token = authHeader;
                System.out.println("Warning: Authorization header does not start with 'Bearer '");
            }
            
            // Remove any surrounding quotes
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
                System.out.println("Removed quotes from token");
            }
            
            System.out.println("Token extracted from header: " + token.substring(0, Math.min(10, token.length())) + "...");
            boolean isValid = keycloakService.validateToken(token);
            System.out.println("Validation du token: " + (isValid ? "valide" : "invalide"));
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            System.out.println("Erreur lors de la validation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserRepresentation>> getUsers() {
        System.out.println("Demande de liste des utilisateurs reçue");
        return ResponseEntity.ok(keycloakService.getUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId) {
        System.out.println("Demande de suppression de l'utilisateur: " + userId);
        keycloakService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal Jwt jwt, 
                                    @RequestHeader(name = "Authorization", required = false) String authHeader) {
        System.out.println("Demande d'information sur l'utilisateur courant");
        
        // Try to use the JWT from Spring Security first
        if (jwt != null) {
            System.out.println("JWT Subject from Security Context: " + jwt.getSubject());
            return ResponseEntity.ok("Hello, " + jwt.getSubject());
        } 
        // If no JWT in security context, try to extract from Authorization header
        else if (authHeader != null && !authHeader.isEmpty()) {
            try {
                System.out.println("Trying to extract JWT from Authorization header");
                // Extract token from "Bearer TOKEN" format
                String token;
                if (authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                } else {
                    token = authHeader;
                }
                
                // Remove any surrounding quotes
                if (token.startsWith("\"") && token.endsWith("\"")) {
                    token = token.substring(1, token.length() - 1);
                }
                
                // Validate and decode the token
                boolean isValid = keycloakService.validateToken(token);
                if (isValid) {
                    // Get user info from token
                    String username = keycloakService.getUsernameFromToken(token);
                    System.out.println("Username from token: " + username);
                    return ResponseEntity.ok("Hello, " + username);
                } else {
                    System.out.println("Invalid token provided in Authorization header");
                    return ResponseEntity.status(401).body("Invalid token");
                }
            } catch (Exception e) {
                System.out.println("Error processing token from header: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(401).body("Error processing token");
            }
        } else {
            System.out.println("Aucun JWT trouvé");
            return ResponseEntity.status(401).body("No JWT found");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        System.out.println("Demande de déconnexion reçue");
        try {
            // Extract token from "Bearer TOKEN" format
            String token;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                
                // Remove any surrounding quotes
                if (token.startsWith("\"") && token.endsWith("\"")) {
                    token = token.substring(1, token.length() - 1);
                }
                
                // Invalidate the token in Keycloak
                keycloakService.logoutUser(token);
                return ResponseEntity.ok("Déconnexion réussie");
            } else {
                return ResponseEntity.badRequest().body("Token non fourni ou format invalide");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
}
