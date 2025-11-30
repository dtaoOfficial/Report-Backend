package com.dtao.alien.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // ✅ Dynamically load allowed origins from .env
    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ✅ Enable broker for both report + notification channels
        config.enableSimpleBroker("/topic/reports", "/topic/notifications");

        // ✅ Prefix for client-to-server destinations (e.g. /app/send)
        config.setApplicationDestinationPrefixes("/app");

        // ✅ Keep message order & heartbeat
        config.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ✅ Split allowed origins from .env (comma-separated)
        String[] origins = allowedOrigins.split(",");

        // ✅ Main WebSocket endpoint (SockJS + native)
        registry.addEndpoint("/ws")
                .setAllowedOrigins(origins) // e.g. http://localhost:3000, https://dtaoofficial.netlify.app
                .setAllowedOriginPatterns("*") // allow both http and https (safe fallback)
                .withSockJS()
                .setHeartbeatTime(10000) // 10s keepalive
                .setSessionCookieNeeded(false); // prevent JSESSIONID cookies for stateless APIs

        // ✅ Optional: native websocket (no SockJS) — modern browsers use this
        registry.addEndpoint("/ws")
                .setAllowedOrigins(origins)
                .setAllowedOriginPatterns("*");
    }
}
