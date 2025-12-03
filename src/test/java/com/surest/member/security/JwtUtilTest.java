package com.surest.member.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User mockUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        mockUser = new User(
                "testuser",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(mockUser);
        assertNotNull(token);
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(mockUser);
        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void testValidateTokenValid() {
        String token = jwtUtil.generateToken(mockUser);
        assertTrue(jwtUtil.validateToken(token, mockUser));
    }

    @Test
    void testValidateTokenInvalidUser() {
        String token = jwtUtil.generateToken(mockUser);

        User otherUser = new User("someoneElse", "pass",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        assertFalse(jwtUtil.validateToken(token, otherUser));
    }
}
