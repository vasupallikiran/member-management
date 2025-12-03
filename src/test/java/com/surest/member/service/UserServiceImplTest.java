package com.surest.member.service;

import com.surest.member.entity.Role;
import com.surest.member.entity.User;
import com.surest.member.exception.UserAlreadyExistsException;
import com.surest.member.repository.UserRepository;
import com.surest.member.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Test
    void existsByUsername() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.existsByUsername("john")).thenReturn(true);

        UserServiceImpl service = new UserServiceImpl(
                mock(AuthenticationManager.class),
                mock(JwtUtil.class),
                mock(UserDetailsService.class),
                repo,
                mock(RoleService.class),
                mock(PasswordEncoder.class)
        );

        assertTrue(service.existsByUsername("john"));
    }

    @Test
    void validateUserLoginDtls_success() {
        AuthenticationManager auth = mock(AuthenticationManager.class);
        JwtUtil jwt = mock(JwtUtil.class);
        UserDetailsService detailsService = mock(UserDetailsService.class);

        UserDetails ud = org.springframework.security.core.userdetails.User
                .withUsername("john")
                .password("pass")
                .authorities("ROLE_ADMIN")
                .build();

        when(detailsService.loadUserByUsername("john")).thenReturn(ud);
        when(jwt.generateToken(ud)).thenReturn("token123");

        UserServiceImpl service = new UserServiceImpl(
                auth,
                jwt,
                detailsService,
                mock(UserRepository.class),
                mock(RoleService.class),
                mock(PasswordEncoder.class)
        );

        String token = service.validateUserLoginDtls("john", "pass");

        assertEquals("token123", token);
        verify(auth).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void registerNewUser_alreadyExists() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.existsByUsername("john")).thenReturn(true);

        UserServiceImpl service = new UserServiceImpl(
                mock(AuthenticationManager.class),
                mock(JwtUtil.class),
                mock(UserDetailsService.class),
                repo,
                mock(RoleService.class),
                mock(PasswordEncoder.class)
        );

        assertThrows(UserAlreadyExistsException.class,
                () -> service.registerNewUser("john", "123", "ADMIN"));
    }

    @Test
    void registerNewUser_success() {
        UserRepository repo = mock(UserRepository.class);
        RoleService roles = mock(RoleService.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        Role role = new Role();
        role.setName("ADMIN");

        when(repo.existsByUsername("john")).thenReturn(false);
        when(roles.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(encoder.encode("123")).thenReturn("ENC");

        User saved = new User();
        saved.setUsername("john");

        when(repo.save(any(User.class))).thenReturn(saved);

        UserServiceImpl service = new UserServiceImpl(
                mock(AuthenticationManager.class),
                mock(JwtUtil.class),
                mock(UserDetailsService.class),
                repo,
                roles,
                encoder
        );

        User result = service.registerNewUser("john", "123", "ADMIN");

        assertEquals("john", result.getUsername());
        verify(repo).save(any(User.class));
    }
}
