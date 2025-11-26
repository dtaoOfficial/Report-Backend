package com.dtao.alien.service;

import com.dtao.alien.model.*;
import com.dtao.alien.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserService userService; // ✅ For fetching user info

    // 🧾 Create a new report (User → System)
    public Report createReport(String title, String description, String location,
                               String createdBy, String createdByName, String department) {

        // ✅ Use the department provided (no forced fallback)
        if (department == null || department.trim().isEmpty()) {
            department = userService.getUserDepartment(createdBy);
        }

        Report report = new Report(title, description, location, createdBy, createdByName, department);

        report.getHistory().add(new ReportHistoryEntry(
                "USER",
                createdByName,
                department,
                "CREATED",
                "Report created by user from " + (department != null ? department : "Unknown")
        ));

        return reportRepository.save(report);
    }

    // 🔍 Get all reports by user
    public List<Report> getReportsByUser(String email) {
        return reportRepository.findByCreatedBy(email);
    }

    // 🔍 Get all reports by stage (System, Principal, Dean, etc.)
    public List<Report> getReportsByStage(ReportStage stage) {
        return reportRepository.findByCurrentStage(stage);
    }

    // 🎓 Get all reports for Principal
    public List<Report> getPrincipalReports() {
        return reportRepository.findByCurrentStage(ReportStage.PRINCIPAL);
    }

    // 🧠 Helper: Lock a role after performing an action
    private void lockRole(Report report, String byRole) {
        if (byRole != null && !report.getLockedByRoles().contains(byRole)) {
            report.getLockedByRoles().add(byRole);
        }
    }

    // ⚙️ Forward report to next stage
    public Report forwardReport(String id, ReportStage nextStage, String byRole,
                                String byName, String comments) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (byRole.equals("ROLE_PRINCIPAL") && nextStage == null) {
            throw new RuntimeException("Next stage not specified for Principal.");
        }

        // 🧭 Dean → Principal fallback
        if (byRole.equals("ROLE_DEAN") && nextStage == null) {
            nextStage = ReportStage.PRINCIPAL;
        }

        // ✅ Department from user performing action (not creator)
        String department = getDepartmentForUserAction(report, byRole);

        report.setCurrentStage(nextStage);
        report.setStatus(ReportStatus.PENDING);
        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                department,
                "FORWARDED",
                comments != null && !comments.isEmpty() ? comments : "Forwarded"
        ));
        report.setUpdatedAt(LocalDateTime.now());

        lockRole(report, byRole);
        return reportRepository.save(report);
    }

    // ✅ Approve report (any stage)
    public Report approveReport(String id, String byRole, String byName, String comments) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        String department = getDepartmentForUserAction(report, byRole);

        report.setStatus(ReportStatus.APPROVED);
        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                department,
                "APPROVED",
                comments != null && !comments.isEmpty() ? comments : "Approved"
        ));
        report.setUpdatedAt(LocalDateTime.now());

        lockRole(report, byRole);
        return reportRepository.save(report);
    }

    // ❌ Reject report
    public Report rejectReport(String id, String byRole, String byName, String reason) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        String department = getDepartmentForUserAction(report, byRole);

        report.setStatus(ReportStatus.REJECTED);
        report.setRejected(true);
        report.setRejectedBy(byRole);
        report.setRejectionReason(reason);
        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                department,
                "REJECTED",
                reason
        ));
        report.setUpdatedAt(LocalDateTime.now());

        lockRole(report, byRole);
        return reportRepository.save(report);
    }

    // 🏁 Mark report completed or unavailable (Resources)
    public Report completeReport(String id, boolean available,
                                 String byRole, String byName, String comments) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        String department = getDepartmentForUserAction(report, byRole);

        report.setStatus(available ? ReportStatus.COMPLETED : ReportStatus.NOT_AVAILABLE);
        report.setActive(false);
        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                department,
                available ? "AVAILABLE" : "NOT_AVAILABLE",
                comments != null ? comments : ""
        ));
        report.setUpdatedAt(LocalDateTime.now());

        lockRole(report, byRole);
        return reportRepository.save(report);
    }

    // 🔍 Get single report
    public Report getReportById(String id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    // 🧠 Get all reports (latest first)
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByUpdatedAtDesc();
    }

    // ✅ Helper: Get department of user performing action (no forced "General")
    private String getDepartmentForUserAction(Report report, String byRole) {
        try {
            String email = report.getCreatedBy(); // default to creator
            String dept = userService.getUserDepartment(email);
            return (dept != null && !dept.isBlank()) ? dept : null;
        } catch (Exception e) {
            return null;
        }
    }
}
