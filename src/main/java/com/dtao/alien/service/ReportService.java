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

    // 🧾 Create a new report (user → system)
    public Report createReport(String title, String description, String location,
                               String createdBy, String createdByName) {
        Report report = new Report(title, description, location, createdBy, createdByName);
        report.getHistory().add(new ReportHistoryEntry("USER", createdByName,
                "CREATED", "Report created by user"));
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

    // 🎓 Get all reports for Principal (handles both PRINCIPAL and FINAL_PRINCIPAL)
    public List<Report> getPrincipalReports() {
        return reportRepository.findByCurrentStageIn(List.of(
                ReportStage.PRINCIPAL,
                ReportStage.FINAL_PRINCIPAL
        ));
    }

    // ⚙️ Forward report to next stage
    public Report forwardReport(String id, ReportStage nextStage, String byRole,
                                String byName, String comments) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // ✅ Respect frontend’s selection; don’t override nextStage
        if (byRole.equals("ROLE_PRINCIPAL")) {
            if (nextStage == null) {
                throw new RuntimeException("Next stage not specified for Principal.");
            }
        }

        report.setCurrentStage(nextStage);
        report.setStatus(ReportStatus.PENDING);
        report.getHistory().add(new ReportHistoryEntry(byRole, byName,
                "FORWARDED", comments));
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // ✅ Approve report (final stage or intermediate)
    public Report approveReport(String id, String byRole, String byName, String comments) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.APPROVED);
        report.getHistory().add(new ReportHistoryEntry(byRole, byName,
                "APPROVED", comments));
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // ❌ Reject report
    public Report rejectReport(String id, String byRole, String byName, String reason) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.REJECTED);
        report.setRejected(true);
        report.setRejectedBy(byRole);
        report.setRejectionReason(reason);
        report.getHistory().add(new ReportHistoryEntry(byRole, byName,
                "REJECTED", reason));
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // 🏁 Mark report completed or unavailable (Resources)
    public Report completeReport(String id, boolean available,
                                 String byRole, String byName, String comments) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(available ? ReportStatus.COMPLETED : ReportStatus.NOT_AVAILABLE);
        report.setActive(false);
        report.getHistory().add(new ReportHistoryEntry(byRole, byName,
                available ? "AVAILABLE" : "NOT_AVAILABLE", comments));
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // 🔍 Get single report
    public Report getReportById(String id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }
}
