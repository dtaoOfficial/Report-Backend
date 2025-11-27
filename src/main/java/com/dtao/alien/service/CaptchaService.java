package com.dtao.alien.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CaptchaService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CAPTCHA_PREFIX = "captcha:";

    public Map<String, String> generateCaptcha() {
        // ✅ CHANGED: Generate 6-digit number only (e.g., "482910")
        String captchaCode = RandomStringUtils.randomNumeric(4);

        String captchaId = UUID.randomUUID().toString();

        // Save to Redis for validation (Expires in 5 mins)
        redisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + captchaId,
                captchaCode,
                Duration.ofMinutes(5)
        );

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        // Sending the number directly (Frontend handles the look)
        response.put("captchaImage", captchaCode);
        return response;
    }

    public boolean validateCaptcha(String captchaId, String captchaAnswer) {
        if (captchaId == null || captchaAnswer == null) return false;

        String cachedCaptcha = redisTemplate.opsForValue().get(CAPTCHA_PREFIX + captchaId);

        // Simple string comparison
        return cachedCaptcha != null && cachedCaptcha.equals(captchaAnswer);
    }
}