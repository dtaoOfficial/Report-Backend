package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.Report;
import com.dtao.alien.model.ReportStage;
import com.dtao.alien.service.ReportService;
import com.dtao.alien.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService; // ✅ For fetching name & department

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // ✅ For WebSocket broadcasting

    // 🧾 USER creates a new report
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Report>> createReport(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // ✅ Fetch user info using UserService
        String name = userService.getUserDisplayName(email);
        String department = userService.getUserDepartment(email);

        Report report = reportService.createReport(title, description, location, email, name, department);

        // 🚀 Notify all connected System users (real-time)
        messagingTemplate.convertAndSend("/topic/reports/system", report);

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

    // 🎓 PRINCIPAL dashboard
    @GetMapping("/principal")
    public ResponseEntity<ApiResponse<List<Report>>> getPrincipalReports() {
        List<Report> reports = reportService.getReportsByStage(ReportStage.PRINCIPAL);
        return ResponseEntity.ok(ApiResponse.success("Fetched principal stage reports", reports));
    }

    // ✅ COMPLETED reports (for system or admin)
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<List<Report>>> getCompletedReports() {
        List<Report> reports = reportService.getReportsByStage(ReportStage.COMPLETED);
        return ResponseEntity.ok(ApiResponse.success("Fetched completed reports", reports));
    }

    // 🔄 FORWARD report to next stage
    @PutMapping("/{id}/forward")
    public ResponseEntity<ApiResponse<Report>> forwardReport(
            @PathVariable String id,
            @RequestParam ReportStage nextStage,
            @RequestParam(required = false) String comments) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();
        String name = userService.getUserDisplayName(email);

        Report updated = reportService.forwardReport(id, nextStage, role, name, comments);

        // 🚀 Real-time broadcast to next stage
        if (nextStage == ReportStage.PRINCIPAL) {
            messagingTemplate.convertAndSend("/topic/reports/principal", updated);
        } else if (nextStage == ReportStage.SYSTEM) {
            messagingTemplate.convertAndSend("/topic/reports/system", updated);
        }

        // ✅ Notify users globally (optional dashboard sync)
        messagingTemplate.convertAndSend("/topic/reports/all", updated);

        return ResponseEntity.ok(ApiResponse.success("Report forwarded successfully", updated));
    }

    // ✅ APPROVE report (Principal)
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Report>> approveReport(
            @PathVariable String id,
            @RequestParam(required = false) String comments) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();
        String name = userService.getUserDisplayName(email);

        Report updated = reportService.approveReport(id, role, name, comments);

        // 🚀 Notify System (to enable "Close Report")
        messagingTemplate.convertAndSend("/topic/reports/system", updated);
        messagingTemplate.convertAndSend("/topic/reports/all", updated);

        return ResponseEntity.ok(ApiResponse.success("Report approved successfully", updated));
    }

    // ❌ REJECT report
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Report>> rejectReport(
            @PathVariable String id,
            @RequestParam String reason) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();
        String name = userService.getUserDisplayName(email);

        Report updated = reportService.rejectReport(id, role, name, reason);

        // 🚀 Notify user + system
        messagingTemplate.convertAndSend("/topic/reports/system", updated);
        messagingTemplate.convertAndSend("/topic/reports/all", updated);

        return ResponseEntity.ok(ApiResponse.success("Report rejected successfully", updated));
    }

    // ✅ SYSTEM closes report after Principal approval
    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<Report>> closeReport(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();
        String name = userService.getUserDisplayName(email);

        String solvedNotes = body.get("solvedNotes");

        Report updated = reportService.completeReportWithNotes(id, solvedNotes, role, name);

        // 🚀 Notify all roles for dashboard refresh
        messagingTemplate.convertAndSend("/topic/reports/system", updated);
        messagingTemplate.convertAndSend("/topic/reports/principal", updated);
        messagingTemplate.convertAndSend("/topic/reports/all", updated);

        return ResponseEntity.ok(ApiResponse.success("Report closed and marked as completed successfully", updated));
    }

    // 🔍 Get single report by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Report>> getReportById(@PathVariable String id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched report successfully", report));
    }

    // 🧠 ALL reports (admin or overview dashboards)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Report>>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success("Fetched all reports successfully", reports));
    }
}
