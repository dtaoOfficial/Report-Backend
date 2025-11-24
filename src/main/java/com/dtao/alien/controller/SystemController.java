package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<String>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success("System Department Dashboard", "Welcome System Department"));
    }
}
