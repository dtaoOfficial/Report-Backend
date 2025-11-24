package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dean")
public class DeanController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('DEAN')")
    public ResponseEntity<ApiResponse<String>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dean Dashboard", "Welcome Dean"));
    }
}
