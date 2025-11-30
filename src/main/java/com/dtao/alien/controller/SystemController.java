package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.Report;
import com.dtao.alien.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<String>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success("System Department Dashboard", "Welcome System Department"));
    }

    // ✅ New endpoint: Close Report (mark as completed with notes)
    @PostMapping("/complete/{id}")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<Report>> completeReport(@PathVariable String id, @RequestBody Map<String, String> body) {
        String solvedNotes = body.get("solvedNotes");
        Report updated = reportService.completeReportWithNotes(id, solvedNotes, "ROLE_SYSTEM", "System Department");
        return ResponseEntity.ok(ApiResponse.success("Report marked as completed successfully", updated));
    }
}
