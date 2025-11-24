package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('RESOURCES')")
    public ResponseEntity<ApiResponse<String>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success("Resource Department Dashboard", "Welcome Resource Department"));
    }
}
