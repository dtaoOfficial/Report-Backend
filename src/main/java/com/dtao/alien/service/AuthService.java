package com.dtao.alien.service;

import com.dtao.alien.dto.request.RegisterRequest;
import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.Gender;
import com.dtao.alien.model.Role;
import com.dtao.alien.model.User;
import com.dtao.alien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private CaptchaService captchaService;

    // --- REGISTER LOGIC ---
    public ApiResponse<String> register(RegisterRequest request) {

        // 1. Validate Captcha (Using CaptchaService)
        boolean isCaptchaValid = captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaAnswer());
        if (!isCaptchaValid) {
            throw new RuntimeException("Invalid or Expired Captcha");
        }

        // 2. Check if Email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        // 3. Custom Gender Validation (Alien/Animal Logic)
        if (request.getGender() == Gender.ANIMAL) {
            if (request.getAnimalName() == null || request.getAnimalName().trim().isEmpty()) {
                throw new RuntimeException("If gender is ANIMAL, you must specify the Animal Name!");
            }
        }

        // 4. Create User Object
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());

        if (request.getGender() == Gender.ANIMAL) {
            user.setAnimalName(request.getAnimalName());
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // --- 5. Assign Role (Default USER or passed one) ---
        Set<Role> assignedRoles = new HashSet<>();

        // If registration request contains a role (optional field)
        if (request.getRole() != null) {
            try {
                Role roleEnum = Role.valueOf(request.getRole().toUpperCase());
                assignedRoles.add(roleEnum);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }
        } else {
            // Default to USER role
            assignedRoles.add(Role.ROLE_USER);
        }

        user.setRoles(assignedRoles);
        user.setVerified(false); // Locked until OTP verified

        userRepository.save(user);

        // 6. Generate & Save OTP (Using OtpService)
        String otp = otpService.generateAndSaveOtp(user.getEmail());

        // 7. Send Email (Using EmailService)
        emailService.sendOtpEmail(user.getEmail(), otp);

        return ApiResponse.success("Registration successful. Please check email for OTP.", null);
    }

    // --- VERIFY OTP LOGIC ---
    public ApiResponse<String> verifyOtp(String email, String otpInput) {
        // 1. Validate OTP (Using OtpService)
        boolean isValid = otpService.validateOtp(email, otpInput);

        if (!isValid) {
            throw new RuntimeException("Invalid or Expired OTP");
        }

        // 2. Activate User in DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        return ApiResponse.success("Account verified! You can now login.", null);
    }

    // --- 8. FORGET PASSWORD (SEND OTP) ---
    @Transactional
    public ApiResponse<String> requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found for this email"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your account before resetting password");
        }

        String otp = otpService.generateAndSaveOtp(email);
        emailService.sendOtpEmail(email, otp);

        return ApiResponse.success("Password reset OTP sent successfully. Check your email.", null);
    }

    // --- 9. VERIFY RESET OTP ---
    public ApiResponse<String> verifyResetOtp(String email, String otp) {
        boolean isValid = otpService.validateOtp(email, otp);
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        return ApiResponse.success("OTP verified successfully. You can now reset your password.", null);
    }

    // --- 10. RESET PASSWORD ---
    @Transactional
    public ApiResponse<String> resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified");
        }

        if (newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ApiResponse.success("Password updated successfully.", null);
    }

    // --- NEW: Fetch user details by email (for controllers) ---
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
