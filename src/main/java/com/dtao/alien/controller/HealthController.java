package com.dtao.alien.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "🟢 Backend is online");
        status.put("app", "DTAO Alien Backend");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
}
