package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.Report;
import com.dtao.alien.model.ReportStatus;
import com.dtao.alien.repository.ReportRepository;
import com.dtao.alien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    // ✅ Admin Dashboard Overview
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<String>> adminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Welcome to Admin Dashboard", "Admin Access Granted"));
    }

    // ✅ Report Statistics for Dashboard
    @GetMapping("/report-stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getReportStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", reportRepository.count());
        stats.put("approved", reportRepository.countByStatus(ReportStatus.APPROVED));
        stats.put("pending", reportRepository.countByStatus(ReportStatus.PENDING));
        stats.put("rejected", reportRepository.countByStatus(ReportStatus.REJECTED));
        stats.put("completed", reportRepository.countByStatus(ReportStatus.COMPLETED));
        return ResponseEntity.ok(ApiResponse.success("Report statistics fetched", stats));
    }

    // ✅ Fetch All Reports for Admin
    @GetMapping("/all-reports")
    public ResponseEntity<ApiResponse<List<Report>>> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("All reports fetched successfully", reports));
    }
}
