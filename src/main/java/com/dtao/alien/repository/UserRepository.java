package com.dtao.alien.repository;

import com.dtao.alien.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // 1. Find a user by email (Used for Login & identifying the user)
    // Returns Optional<> to avoid NullPointerExceptions
    Optional<User> findByEmail(String email);

    // 2. High-Performance Check (Used during Registration)
    // MongoDB will only check the index, it won't fetch the document. VERY FAST.
    Boolean existsByEmail(String email);

    // 3. Check if phone number is already taken (Optional validation)
    Boolean existsByPhoneNumber(String phoneNumber);
}