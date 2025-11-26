package com.dtao.alien.controller;

import com.dtao.alien.dto.request.AdminCreateUserRequest;
import com.dtao.alien.dto.response.ApiResponse;
import com.dtao.alien.model.User;
import com.dtao.alien.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // ✅ Get All Users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // ✅ Create New User (Supports department for ROLE_USER)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody AdminCreateUserRequest request) {

        // 🏫 Validation: Department required for ROLE_USER
        if ((request.getRole() != null && request.getRole().name().equals("ROLE_USER")) ||
                (request.getRoles() != null && request.getRoles().stream().anyMatch(r -> r.name().equals("ROLE_USER")))) {

            if (request.getDepartment() == null || request.getDepartment().trim().isEmpty()) {
                throw new RuntimeException("Department is required for ROLE_USER");
            }
        }

        return ResponseEntity.ok(adminUserService.createUser(request));
    }

    // ✅ Delete User
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(adminUserService.deleteUser(id));
    }

    // ✅ Update Existing User (Supports department)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable String id,
            @RequestBody User updatedUser
    ) {
        // 🏫 Optional check: prevent admin from clearing department for normal users
        if ((updatedUser.getRoles() != null && updatedUser.getRoles().stream().anyMatch(r -> r.name().equals("ROLE_USER"))) &&
                (updatedUser.getDepartment() == null || updatedUser.getDepartment().trim().isEmpty())) {
            throw new RuntimeException("Department cannot be empty for ROLE_USER");
        }

        User savedUser = adminUserService.updateUser(id, updatedUser);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", savedUser));
    }
}
