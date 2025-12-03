package com.surest.member.util;

import com.surest.member.entity.Role;
import com.surest.member.entity.User;
import com.surest.member.repository.RoleRepository;
import com.surest.member.repository.UserRepository;
import com.surest.member.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public TestUtil(RoleRepository roleRepository, UserRepository userRepository,
                    PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String createTestUser(String username, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName)));

        User existing = userRepository.findByUsername(username).orElse(null);
        if (existing != null) {
            return jwtUtil.generateToken(
                    org.springframework.security.core.userdetails.User
                            .withUsername(username)
                            .password(existing.getPasswordHash())
                            .roles(roleName)
                            .build()
            );
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(role);
        userRepository.save(user);

        return jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password(user.getPasswordHash())
                        .roles(roleName)
                        .build()
        );
    }

}
