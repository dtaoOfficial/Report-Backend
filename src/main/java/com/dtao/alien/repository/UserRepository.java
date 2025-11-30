package com.dtao.alien.repository;

import com.dtao.alien.model.Role;
import com.dtao.alien.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // 1️⃣ Find user by email (used for login)
    Optional<User> findByEmail(String email);

    // 2️⃣ Check if email already exists
    Boolean existsByEmail(String email);

    // 3️⃣ Optional: check if phone number exists
    Boolean existsByPhoneNumber(String phoneNumber);

    // ✅ FIXED: use plural "roles" and correct type Role (enum)
    Optional<User> findFirstByRoles(Role role);
}
