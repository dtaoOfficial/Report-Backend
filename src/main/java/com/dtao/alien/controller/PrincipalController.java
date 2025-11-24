package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/principal")
public class PrincipalController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse<String>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success("Principal Dashboard", "Welcome Principal"));
    }
}
