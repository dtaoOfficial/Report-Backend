package com.dtao.alien.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_EXPIRATION_MINUTES = 5;

    // Generate and Save OTP
    public String generateAndSaveOtp(String email) {
        String otp = RandomStringUtils.randomNumeric(6);
        redisTemplate.opsForValue().set(
                OTP_PREFIX + email,
                otp,
                Duration.ofMinutes(OTP_EXPIRATION_MINUTES)
        );
        return otp;
    }

    // Validate and Delete OTP
    public boolean validateOtp(String email, String otpInput) {
        String cachedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + email);
        if (cachedOtp != null && cachedOtp.equals(otpInput)) {
            redisTemplate.delete(OTP_PREFIX + email); // One-time use
            return true;
        }
        return false;
    }
}