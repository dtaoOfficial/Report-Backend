package com.dtao.alien.controller;

import com.dtao.alien.dto.request.LoginRequest;
import com.dtao.alien.dto.request.RegisterRequest;
import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.User;
import com.dtao.alien.security.JwtUtil;
import com.dtao.alien.service.AuthService;
import com.dtao.alien.service.CaptchaService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CaptchaService captchaService;

    // NOTE: /get-captcha is now handled by CaptchaController.java

    // --- 1. REGISTER USER ---
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // --- 2. VERIFY OTP ---
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return ResponseEntity.ok(authService.verifyOtp(email, otp));
    }

    // --- 3. LOGIN (Sets HttpOnly Cookie + Return Role Info) ---
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

        // A. Validate Captcha First (Using Service)
        boolean isCaptchaValid = captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaAnswer());
        if (!isCaptchaValid) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or Expired Captcha", null));
        }

        // B. Authenticate with Spring Security
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid Credentials", null));
        }

        // C. Generate JWT
        String token = jwtUtil.generateToken(request.getEmail());

        // D. Create HttpOnly Cookie
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true); // JavaScript cannot read this (XSS Protection)
        cookie.setSecure(false);  // Set TRUE in Production (Requires HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 Day

        // E. Add Cookie to Response
        response.addCookie(cookie);

        // F. Fetch user info to include roles in response
        User user = authService.getUserByEmail(request.getEmail());

        // G. Return success + role data for frontend routing
        return ResponseEntity.ok(
                ApiResponse.success("Login Successful", user.getRoles())
        );
    }

    // --- 4. LOGOUT ---
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete immediately
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    // --- 5. FORGOT PASSWORD (Send OTP) ---
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.requestPasswordReset(email));
    }

    // --- 6. VERIFY RESET OTP ---
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<String>> verifyResetOtp(@RequestParam String email, @RequestParam String otp) {
        return ResponseEntity.ok(authService.verifyResetOtp(email, otp));
    }

    // --- 7. RESET PASSWORD ---
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        return ResponseEntity.ok(authService.resetPassword(email, newPassword));
    }
}
