package org.sid.authservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur d'urgence ultra-simplifié sans aucune dépendance
 */
@RestController
public class EmergencyController {

    /**
     * Endpoint de test basique
     */
    @GetMapping("/emergency/test")
    public String test() {
        return "Emergency controller is working!";
    }

    /**
     * Endpoint de login d'urgence
     */
    @PostMapping("/emergency/login")
    public Map<String, Object> login() {
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", "emergency_token_123456789");
        response.put("token", "emergency_token_123456789");
        return response;
    }
    
    /**
     * Endpoint pour récupérer les informations de l'utilisateur
     */
    @GetMapping("/emergency/me")
    public Map<String, Object> me() {
        System.out.println("=== REQUÊTE ME REÇUE DANS EMERGENCY CONTROLLER ===");
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "hamza");
        userInfo.put("email", "hamza@example.com");
        userInfo.put("roles", new String[]{"ROLE_USER"});
        return userInfo;
    }
}
