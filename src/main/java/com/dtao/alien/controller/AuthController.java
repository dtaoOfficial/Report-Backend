package com.dtao.alien.controller;

import com.dtao.alien.dto.request.LoginRequest;
import com.dtao.alien.dto.request.RegisterRequest;
import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.User;
import com.dtao.alien.security.JwtUtil;
import com.dtao.alien.service.AuthService;
import com.dtao.alien.service.CaptchaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

        // ✅ A. Validate Captcha
        boolean isCaptchaValid = captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaAnswer());
        if (!isCaptchaValid) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or Expired Captcha", null));
        }

        // ✅ B. Authenticate User
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid Credentials", null));
        }

        // ✅ C. Generate JWT
        String token = jwtUtil.generateToken(request.getEmail());

        // ✅ D. Create HttpOnly Cookie for security (optional frontend use)
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true) // true for HTTPS; false for localhost testing
                .sameSite("None")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // ✅ E. Get User Details
        User user = authService.getUserByEmail(request.getEmail());
        String role = user.getRoles().iterator().next().name(); // e.g. ROLE_SYSTEM

        // ✅ F. Build Response Payload
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        data.put("role", role);

        // ✅ G. Return Unified API Response
        return ResponseEntity.ok(ApiResponse.success("Login Successful", data));
    }

    // --- 4. LOGOUT ---
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
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
