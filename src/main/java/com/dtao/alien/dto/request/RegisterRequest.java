package com.dtao.alien.dto.request;

import com.dtao.alien.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber; // Optional

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Gender is required")
    private Gender gender; // MALE, FEMALE, ALIEN, GOD, ANIMAL

    private String animalName; // Mandatory only if Gender is ANIMAL

    @NotBlank(message = "Captcha answer is required")
    private String captchaAnswer;

    private String captchaId; // To verify against Redis

    // ✅ NEW FIELD — Optional Role Selection (for Admins or setup)
    private String role; // Optional: e.g. "ROLE_SYSTEM", "ROLE_PRINCIPAL"

    // --- Constructors, Getters, Setters (No Lombok) ---

    public RegisterRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAnimalName() { return animalName; }
    public void setAnimalName(String animalName) { this.animalName = animalName; }

    public String getCaptchaAnswer() { return captchaAnswer; }
    public void setCaptchaAnswer(String captchaAnswer) { this.captchaAnswer = captchaAnswer; }

    public String getCaptchaId() { return captchaId; }
    public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
