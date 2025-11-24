package com.dtao.alien.service;

import com.dtao.alien.model.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final String REDIS_KEY_PREFIX = "notifications:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 📨 Send a notification to a role (e.g., PRINCIPAL, DEAN)
     */
    public void sendNotification(String sender, String recipientRole, String message, String type) {
        NotificationMessage notification = new NotificationMessage(sender, recipientRole, message, type);
        notification.setId(UUID.randomUUID().toString());

        // ✅ Store in Redis temporarily
        String redisKey = REDIS_KEY_PREFIX + recipientRole;
        List<NotificationMessage> existing = getNotifications(recipientRole);
        existing.add(notification);
        redisTemplate.opsForValue().set(redisKey, existing);

        // ✅ Push via WebSocket in real time
        messagingTemplate.convertAndSend("/topic/notifications/" + recipientRole, notification);
    }

    /**
     * 📥 Get all notifications for a specific role
     */
    @SuppressWarnings("unchecked")
    public List<NotificationMessage> getNotifications(String recipientRole) {
        String redisKey = REDIS_KEY_PREFIX + recipientRole;
        Object stored = redisTemplate.opsForValue().get(redisKey);
        if (stored instanceof List) {
            return (List<NotificationMessage>) stored;
        }
        return new ArrayList<>();
    }

    /**
     * ✅ Mark all notifications as read
     */
    public void markAllAsRead(String recipientRole) {
        List<NotificationMessage> notifications = getNotifications(recipientRole);
        for (NotificationMessage n : notifications) {
            n.setRead(true);
        }
        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + recipientRole, notifications);
    }

    /**
     * 🧹 Clear all notifications (optional)
     */
    public void clearNotifications(String recipientRole) {
        redisTemplate.delete(REDIS_KEY_PREFIX + recipientRole);
    }
}
