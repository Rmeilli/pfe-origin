package com.example.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String> root() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Auth service is running");
        return response;
    }
    
    @GetMapping("/api/status")
    public Map<String, String> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "auth-service");
        return response;
    }
} 