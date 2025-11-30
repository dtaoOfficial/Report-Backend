package com.dtao.alien.model;

import java.time.LocalDateTime;

public class ReportHistoryEntry {

    private String byRole;
    private String byName;
    private String byDepartment;

    private String toDepartment; // ✅ NEW
    private String toName;       // ✅ NEW

    private String action;
    private String comments;
    private LocalDateTime timestamp;

    public ReportHistoryEntry() {}

    public ReportHistoryEntry(String byRole, String byName, String byDepartment,
                              String toDepartment, String toName,
                              String action, String comments) {
        this.byRole = byRole;
        this.byName = byName;
        this.byDepartment = byDepartment;
        this.toDepartment = toDepartment;
        this.toName = toName;
        this.action = action;
        this.comments = comments;
        this.timestamp = LocalDateTime.now();
    }

    // Existing short constructor (backward compatibility)
    public ReportHistoryEntry(String byRole, String byName, String byDepartment, String action, String comments) {
        this(byRole, byName, byDepartment, null, null, action, comments);
    }

    // --- Getters & Setters ---
    public String getByRole() { return byRole; }
    public void setByRole(String byRole) { this.byRole = byRole; }

    public String getByName() { return byName; }
    public void setByName(String byName) { this.byName = byName; }

    public String getByDepartment() { return byDepartment; }
    public void setByDepartment(String byDepartment) { this.byDepartment = byDepartment; }

    public String getToDepartment() { return toDepartment; }
    public void setToDepartment(String toDepartment) { this.toDepartment = toDepartment; }

    public String getToName() { return toName; }
    public void setToName(String toName) { this.toName = toName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
