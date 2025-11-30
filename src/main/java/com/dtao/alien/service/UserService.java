package com.dtao.alien.service;

import com.dtao.alien.model.Role;
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

    // ================================================================
    // ✅ Helpers for history and "toDepartment / toName" formatting
    // ================================================================

    /**
     * 🎓 Get Principal's name (used when forwarding to Principal)
     */
    public String getPrincipalName() {
        try {
            User principal = userRepository.findFirstByRoles(Role.ROLE_PRINCIPAL).orElse(null);
            return principal != null ? principal.getFullName() : "Principal";
        } catch (Exception e) {
            return "Principal";
        }
    }

    /**
     * 🖥️ Get System Department user's name (used when forwarding to System)
     */
    public String getSystemUserName() {
        try {
            User systemUser = userRepository.findFirstByRoles(Role.ROLE_SYSTEM).orElse(null);
            return systemUser != null ? systemUser.getFullName() : "System User";
        } catch (Exception e) {
            return "System User";
        }
    }
}
