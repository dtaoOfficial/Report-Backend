package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.NotificationMessage;
import com.dtao.alien.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 📨 Send a test or manual notification
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendNotification(
            @RequestParam String sender,
            @RequestParam String recipientRole,
            @RequestParam String message,
            @RequestParam(defaultValue = "INFO") String type) {

        notificationService.sendNotification(sender, recipientRole, message, type);
        return ResponseEntity.ok(ApiResponse.success("Notification sent successfully", null));
    }

    /**
     * 📥 Fetch all notifications for a specific role
     */
    @GetMapping("/{role}")
    public ResponseEntity<ApiResponse<List<NotificationMessage>>> getNotifications(
            @PathVariable String role) {

        List<NotificationMessage> notifications = notificationService.getNotifications(role.toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Fetched notifications successfully", notifications));
    }

    /**
     * ✅ Mark all notifications as read
     */
    @PutMapping("/{role}/mark-read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@PathVariable String role) {
        notificationService.markAllAsRead(role.toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    /**
     * 🧹 Clear all notifications (optional)
     */
    @DeleteMapping("/{role}/clear")
    public ResponseEntity<ApiResponse<String>> clearNotifications(@PathVariable String role) {
        notificationService.clearNotifications(role.toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("All notifications cleared", null));
    }

    /**
     * 💬 WebSocket real-time route (for testing in WebSocket clients)
     */
    @MessageMapping("/send")
    @SendTo("/topic/notifications/public")
    public NotificationMessage broadcastNotification(NotificationMessage message) {
        return message;
    }
}
