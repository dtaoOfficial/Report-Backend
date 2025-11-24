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

        // A. Validate Captcha First
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

        // ✅ D. Create HttpOnly Cookie for Render (Cross-Origin Safe)
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true) // HTTPS required on Render
                .sameSite("None") // Allow from Netlify/localhost
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        // ✅ E. Add Cookie Header
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

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
