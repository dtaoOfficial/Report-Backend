package com.dtao.alien.controller;

import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/get-captcha")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCaptcha() {
        return ResponseEntity.ok(ApiResponse.success("Captcha Generated", captchaService.generateCaptcha()));
    }
}