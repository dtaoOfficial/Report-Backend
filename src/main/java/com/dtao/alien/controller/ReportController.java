package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.Report;
import com.dtao.alien.model.ReportStage;
import com.dtao.alien.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // 🧾 USER creates a new report
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Report>> createReport(@RequestParam String title,
                                                            @RequestParam String description,
                                                            @RequestParam String location) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String name = email.split("@")[0]; // can be improved by fetching from UserRepository

        Report report = reportService.createReport(title, description, location, email, name);
        return ResponseEntity.ok(ApiResponse.success("Report created successfully", report));
    }

    // 👤 USER views own reports
    @GetMapping("/my-reports")
    public ResponseEntity<ApiResponse<List<Report>>> getMyReports() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Report> reports = reportService.getReportsByUser(email);
        return ResponseEntity.ok(ApiResponse.success("Fetched user reports", reports));
    }

    // ⚙️ SYSTEM dashboard - fetch reports waiting for System stage
    @GetMapping("/system")
    public ResponseEntity<ApiResponse<List<Report>>> getSystemReports() {
        List<Report> reports = reportService.getReportsByStage(ReportStage.SYSTEM);
        return ResponseEntity.ok(ApiResponse.success("Fetched system stage reports", reports));
    }

    // 🎓 PRINCIPAL dashboard (Handles both PRINCIPAL and FINAL_PRINCIPAL stages)
    @GetMapping("/principal")
    public ResponseEntity<ApiResponse<List<Report>>> getPrincipalReports() {
        List<Report> reports = reportService.getPrincipalReports();
        return ResponseEntity.ok(ApiResponse.success("Fetched principal stage reports", reports));
    }

    // 🧑‍💼 DEAN dashboard
    @GetMapping("/dean")
    public ResponseEntity<ApiResponse<List<Report>>> getDeanReports() {
        List<Report> reports = reportService.getReportsByStage(ReportStage.DEAN);
        return ResponseEntity.ok(ApiResponse.success("Fetched dean stage reports", reports));
    }

    // 🏗️ RESOURCES dashboard
    @GetMapping("/resources")
    public ResponseEntity<ApiResponse<List<Report>>> getResourceReports() {
        List<Report> reports = reportService.getReportsByStage(ReportStage.RESOURCES);
        return ResponseEntity.ok(ApiResponse.success("Fetched resource stage reports", reports));
    }

    // 🔄 FORWARD report to next stage
    @PutMapping("/{id}/forward")
    public ResponseEntity<ApiResponse<Report>> forwardReport(
            @PathVariable String id,
            @RequestParam ReportStage nextStage,
            @RequestParam String comments) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority();
        String name = email.split("@")[0];

        Report updated = reportService.forwardReport(id, nextStage, role, name, comments);
        return ResponseEntity.ok(ApiResponse.success("Report forwarded successfully", updated));
    }

    // ✅ APPROVE report
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Report>> approveReport(
            @PathVariable String id,
            @RequestParam String comments) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority();
        String name = email.split("@")[0];

        Report updated = reportService.approveReport(id, role, name, comments);
        return ResponseEntity.ok(ApiResponse.success("Report approved", updated));
    }

    // ❌ REJECT report
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Report>> rejectReport(
            @PathVariable String id,
            @RequestParam String reason) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority();
        String name = email.split("@")[0];

        Report updated = reportService.rejectReport(id, role, name, reason);
        return ResponseEntity.ok(ApiResponse.success("Report rejected", updated));
    }

    // 🏁 RESOURCES completes report
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Report>> completeReport(
            @PathVariable String id,
            @RequestParam boolean available,
            @RequestParam String comments) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority();
        String name = email.split("@")[0];

        Report updated = reportService.completeReport(id, available, role, name, comments);
        return ResponseEntity.ok(ApiResponse.success("Report completed", updated));
    }

    // 🔍 Get single report by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Report>> getReportById(@PathVariable String id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched report successfully", report));
    }
}
