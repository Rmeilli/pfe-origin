package org.sid.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> authServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Le service d'authentification n'est pas disponible actuellement. Veuillez réessayer plus tard.");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/cre")
    public ResponseEntity<Map<String, String>> creServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Le service CRE n'est pas disponible actuellement. Veuillez réessayer plus tard.");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/frontend")
    public ResponseEntity<Map<String, String>> frontendFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "L'interface utilisateur n'est pas disponible actuellement. Veuillez réessayer plus tard.");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}