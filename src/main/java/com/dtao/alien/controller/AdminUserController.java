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

    // ✅ Create New User
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody AdminCreateUserRequest request) {
        return ResponseEntity.ok(adminUserService.createUser(request));
    }

    // ✅ Delete User
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(adminUserService.deleteUser(id));
    }

    // --- UPDATE EXISTING USER ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable String id,
            @RequestBody User updatedUser
    ) {
        User savedUser = adminUserService.updateUser(id, updatedUser);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", savedUser));
    }


}
