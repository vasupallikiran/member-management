package com.surest.member.service;

import com.surest.member.entity.Role;
import com.surest.member.entity.User;
import com.surest.member.exception.InvalidUserException;
import com.surest.member.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Test
    void loadUserByUsername_success() {
        UserRepository repo = mock(UserRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repo);

        Role role = new Role();
        role.setName("ADMIN");

        User user = new User();
        user.setUsername("john");
        user.setPasswordHash("pass");
        user.setRole(role);

        when(repo.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john");

        assertEquals("john", details.getUsername());
        assertEquals("pass", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_notFound() {
        UserRepository repo = mock(UserRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repo);

        when(repo.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> service.loadUserByUsername("john"));
    }
}
