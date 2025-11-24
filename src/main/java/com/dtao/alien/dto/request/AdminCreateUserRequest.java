package com.dtao.alien.dto.request;

import com.dtao.alien.model.Gender;
import com.dtao.alien.model.Role;
import java.util.Set;

public class AdminCreateUserRequest {

    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private Gender gender;
    private String animalName; // Optional
    private Role role;         // ✅ single role (old support)
    private Set<Role> roles;   // ✅ new array support (frontend sends this)

    // --- Getters & Setters ---
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

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}
