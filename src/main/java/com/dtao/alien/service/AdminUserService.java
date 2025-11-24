package com.dtao.alien.service;

import com.dtao.alien.dto.request.AdminCreateUserRequest;
import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.Gender;
import com.dtao.alien.model.Role;
import com.dtao.alien.model.User;
import com.dtao.alien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Create new user by Admin
    public ApiResponse<String> createUser(AdminCreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getGender() == Gender.ANIMAL &&
                (request.getAnimalName() == null || request.getAnimalName().trim().isEmpty())) {
            throw new RuntimeException("Animal name required if gender is ANIMAL");
        }

        String password = (request.getPassword() == null || request.getPassword().isEmpty())
                ? UUID.randomUUID().toString().substring(0, 8) // default random password
                : request.getPassword();

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setAnimalName(request.getAnimalName());
        user.setPassword(passwordEncoder.encode(password));

        // ✅ Handle both single role and multiple roles gracefully
        if (request.getRole() != null) {
            user.setRoles(Collections.singleton(request.getRole()));
        } else if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(request.getRoles());
        } else {
            throw new RuntimeException("At least one role is required");
        }

        user.setVerified(true); // Admin-created users are verified
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return ApiResponse.success("User created successfully", null);
    }

    // ✅ Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Delete user
    public ApiResponse<String> deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
        return ApiResponse.success("User deleted successfully", null);
    }

    // ✅ Update existing user
    public User updateUser(String id, User updatedUser) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update basic fields
        existing.setFullName(updatedUser.getFullName());
        existing.setPhoneNumber(updatedUser.getPhoneNumber());
        existing.setGender(updatedUser.getGender());
        existing.setAnimalName(updatedUser.getAnimalName());

        // Update password only if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // ✅ Only update roles if provided and not empty (to prevent wiping)
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existing.setRoles(updatedUser.getRoles());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existing);
    }
}
