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
        String captchaCode = RandomStringUtils.randomAlphanumeric(6);
        String captchaId = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + captchaId,
                captchaCode,
                Duration.ofMinutes(2)
        );

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        response.put("captchaImage", captchaCode); // In real app, convert to Base64 Image
        return response;
    }

    public boolean validateCaptcha(String captchaId, String captchaAnswer) {
        String cachedCaptcha = redisTemplate.opsForValue().get(CAPTCHA_PREFIX + captchaId);
        return cachedCaptcha != null && cachedCaptcha.equals(captchaAnswer);
    }
}