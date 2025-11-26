package com.dtao.alien.service;

import com.dtao.alien.model.User;
import com.dtao.alien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 🧠 Fetch user by email safely (used by ReportController)
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * 🧾 Get user’s full display name (fallback to email prefix)
     */
    public String getUserDisplayName(String email) {
        try {
            User user = getUserByEmail(email);
            return user.getFullName() != null && !user.getFullName().isEmpty()
                    ? user.getFullName()
                    : email.split("@")[0];
        } catch (Exception e) {
            return email.split("@")[0];
        }
    }

    /**
     * 🏫 Get user’s department for reports (defaults to "General" if null)
     */
    public String getUserDepartment(String email) {
        try {
            User user = getUserByEmail(email);
            return user.getDepartment() != null && !user.getDepartment().isEmpty()
                    ? user.getDepartment()
                    : "General";
        } catch (Exception e) {
            return "General";
        }
    }
}
