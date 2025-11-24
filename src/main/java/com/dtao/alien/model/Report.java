package com.dtao.alien.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "reports")
public class Report {

    @Id
    private String id;

    private String title;
    private String description;
    private String location;

    private String createdBy;     // email or user id
    private String createdByName; // display name of reporter

    private ReportStatus status;      // PENDING, APPROVED, REJECTED, COMPLETED
    private ReportStage currentStage; // USER, SYSTEM, PRINCIPAL, DEAN, FINAL_PRINCIPAL, RESOURCES

    // --- For rejection tracking ---
    private boolean rejected;
    private String rejectedBy;        // which role rejected
    private String rejectionReason;   // reason text

    // --- For timeline/history ---
    private List<ReportHistoryEntry> history = new ArrayList<>();

    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // --- Constructors ---
    public Report() {}

    public Report(String title, String description, String location, String createdBy, String createdByName) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.status = ReportStatus.PENDING;
        this.currentStage = ReportStage.SYSTEM; // first stage after user submits
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public ReportStage getCurrentStage() { return currentStage; }
    public void setCurrentStage(ReportStage currentStage) { this.currentStage = currentStage; }

    public boolean isRejected() { return rejected; }
    public void setRejected(boolean rejected) { this.rejected = rejected; }

    public String getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(String rejectedBy) { this.rejectedBy = rejectedBy; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public List<ReportHistoryEntry> getHistory() { return history; }
    public void setHistory(List<ReportHistoryEntry> history) { this.history = history; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
