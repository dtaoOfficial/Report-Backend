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

        if (department == null || department.trim().isEmpty()) {
            department = userService.getUserDepartment(createdBy);
        }

        Report report = new Report(title, description, location, createdBy, createdByName, department);

        report.getHistory().add(new ReportHistoryEntry(
                "USER",
                createdByName,
                department,
                null,
                null,
                "CREATED",
                "Report created by user from " + (department != null ? department : "Unknown")
        ));

        return reportRepository.save(report);
    }

    // 🔍 Get all reports by user
    public List<Report> getReportsByUser(String email) {
        return reportRepository.findByCreatedBy(email);
    }

    // 🔍 Get all reports by stage (System, Principal)
    public List<Report> getReportsByStage(ReportStage stage) {
        return reportRepository.findByCurrentStage(stage);
    }

    // 🧠 Lock a role after performing an action
    private void lockRole(Report report, String byRole) {
        if (byRole != null && !report.getLockedByRoles().contains(byRole)) {
            report.getLockedByRoles().add(byRole);
        }
    }

    // ⚙️ Forward report (adds clear “from → to” info)
    public Report forwardReport(String id, ReportStage nextStage, String byRole,
                                String byName, String comments) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Determine "from" data
        String fromDepartment = getDepartmentForUserAction(report, byRole);

        // Determine "to" data (based on next stage)
        String toDepartment = "";
        String toName = "";

        switch (nextStage) {
            case PRINCIPAL -> {
                toDepartment = "Principal";
                toName = userService.getPrincipalName();
            }
            case SYSTEM -> {
                toDepartment = "System";
                toName = userService.getSystemUserName();
            }
            case COMPLETED -> {
                toDepartment = "System";
                toName = userService.getSystemUserName();
            }
            default -> {
                toDepartment = fromDepartment;
                toName = byName;
            }
        }

        // Update report state
        report.setCurrentStage(nextStage);
        report.setStatus(ReportStatus.PENDING);

        // 🧠 Add clear history entry
        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                fromDepartment,
                toDepartment,
                toName,
                "FORWARDED",
                (comments != null && !comments.isEmpty()) ? comments : "Forwarded"
        ));

        report.setUpdatedAt(LocalDateTime.now());
        lockRole(report, byRole);

        return reportRepository.save(report);
    }

    // ✅ Approve report (Principal)
    public Report approveReport(String id, String byRole, String byName, String comments) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        String fromDepartment = getDepartmentForUserAction(report, byRole);

        report.setStatus(ReportStatus.APPROVED);

        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                fromDepartment,
                null,
                null,
                "APPROVED",
                comments != null && !comments.isEmpty() ? comments : "Approved"
        ));

        report.setUpdatedAt(LocalDateTime.now());
        lockRole(report, byRole);
        return reportRepository.save(report);
    }

    // ❌ Reject report (Principal)
    public Report rejectReport(String id, String byRole, String byName, String reason) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        String fromDepartment = getDepartmentForUserAction(report, byRole);

        report.setStatus(ReportStatus.REJECTED);
        report.setRejected(true);
        report.setRejectedBy(byRole);
        report.setRejectionReason(reason);

        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                fromDepartment,
                null,
                null,
                "REJECTED",
                reason
        ));

        report.setUpdatedAt(LocalDateTime.now());
        lockRole(report, byRole);
        return reportRepository.save(report);
    }

    // ✅ System closes / completes report
    public Report completeReportWithNotes(String id, String solvedNotes, String byRole, String byName) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        String fromDepartment = getDepartmentForUserAction(report, byRole);

        report.setStatus(ReportStatus.COMPLETED);
        report.setCurrentStage(ReportStage.COMPLETED);
        report.setActive(false);
        report.setSolvedNotes(solvedNotes);

        report.getHistory().add(new ReportHistoryEntry(
                byRole,
                byName,
                fromDepartment,
                null,
                null,
                "COMPLETED",
                solvedNotes != null ? solvedNotes : "Marked as completed by System"
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

    // 🔧 Helper — safely get department of current user performing the action
    private String getDepartmentForUserAction(Report report, String byRole) {
        try {
            String email = report.getCreatedBy();
            String dept = userService.getUserDepartment(email);
            return (dept != null && !dept.isBlank()) ? dept : "General";
        } catch (Exception e) {
            return "General";
        }
    }
}
