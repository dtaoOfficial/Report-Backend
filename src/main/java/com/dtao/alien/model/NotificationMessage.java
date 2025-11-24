package com.dtao.alien.model;

import java.time.LocalDateTime;

public class NotificationMessage {

    private String id;
    private String sender;
    private String recipientRole; // e.g. SYSTEM, PRINCIPAL, DEAN, RESOURCES, ADMIN
    private String message;
    private String type; // e.g. "FORWARD", "APPROVE", "REJECT", "INFO"
    private boolean read;
    private LocalDateTime timestamp;

    public NotificationMessage() {
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public NotificationMessage(String sender, String recipientRole, String message, String type) {
        this.sender = sender;
        this.recipientRole = recipientRole;
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
