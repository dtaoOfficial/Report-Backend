package com.dtao.alien.config;

import com.dtao.alien.model.Gender;
import com.dtao.alien.model.Role;
import com.dtao.alien.model.User;
import com.dtao.alien.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "imsingle8688@gmail.com";

            // Check if admin already exists
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setId(UUID.randomUUID().toString()); // Unique ID for MongoDB
                admin.setFullName("KURAPARTHI MAHESWAR REDDY");
                admin.setEmail(adminEmail);
                admin.setPhoneNumber("+91" + (long) (Math.random() * 9000000000L + 1000000000L));
                admin.setPassword(passwordEncoder.encode("Mahesh@8688"));
                admin.setGender(Gender.ALIEN);
                admin.setAnimalName("Cosmic Lion");
                admin.setRoles(Set.of(Role.ROLE_ADMIN));
                admin.setVerified(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());

                // Remove version conflict by not setting version manually
                // MongoDB will handle it automatically when needed

                userRepository.insert(admin); // Use insert() instead of save() to avoid version conflict

                System.out.println("✅ Default Admin Created Successfully!");
                System.out.println("------------------------------------");
                System.out.println("📧 Email: " + admin.getEmail());
                System.out.println("🔑 Password: Mahesh@8688");
                System.out.println("🪪 Role: ROLE_ADMIN");
                System.out.println("🌐 Login URL: http://localhost:3000/login");
                System.out.println("------------------------------------");
            } else {
                System.out.println("⚙️ Admin user already exists, skipping creation.");
            }
        };
    }
}