package org.sid.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/home")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Home controller in correct package");
        return response;
    }
} 