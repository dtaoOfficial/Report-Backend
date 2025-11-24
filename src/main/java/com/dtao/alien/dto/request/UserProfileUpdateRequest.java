package com.dtao.alien.dto.request;

import com.dtao.alien.model.Gender;
import jakarta.validation.constraints.NotBlank;

public class UserProfileUpdateRequest {

    @NotBlank(message = "Full name cannot be empty")
    private String fullName;

    private String phoneNumber;

    private Gender gender;
    
    private String animalName;

    // --- Getters and Setters ---
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAnimalName() { return animalName; }
    public void setAnimalName(String animalName) { this.animalName = animalName; }
}