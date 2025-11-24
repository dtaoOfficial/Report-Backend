package com.dtao.alien.model;

import java.time.LocalDateTime;

public class ReportHistoryEntry {

    private String byRole;
    private String byName;
    private String action;      // FORWARDED, APPROVED, REJECTED
    private String comments;
    private LocalDateTime timestamp;

    public ReportHistoryEntry() {}

    public ReportHistoryEntry(String byRole, String byName, String action, String comments) {
        this.byRole = byRole;
        this.byName = byName;
        this.action = action;
        this.comments = comments;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getByRole() { return byRole; }
    public void setByRole(String byRole) { this.byRole = byRole; }

    public String getByName() { return byName; }
    public void setByName(String byName) { this.byName = byName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
