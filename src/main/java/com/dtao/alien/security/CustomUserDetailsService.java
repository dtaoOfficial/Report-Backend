package com.dtao.alien.security;

import com.dtao.alien.model.User;
import com.dtao.alien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        // 🔍 DEBUG: See what is coming from Database
        System.out.println("🔍 RAW DB ROLES for " + email + ": " + user.getRoles());

        var authorities = user.getRoles().stream()
                .map(role -> {
                    String roleName = role.name();

                    // ✅ SMART FIX: Only add "ROLE_" if it is NOT there already
                    // This prevents "ROLE_ROLE_ADMIN" errors
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }

                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());

        // 🔍 DEBUG: See what Spring Security is actually getting
        System.out.println("✅ FINAL MAPPED AUTHORITIES: " + authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isVerified(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}