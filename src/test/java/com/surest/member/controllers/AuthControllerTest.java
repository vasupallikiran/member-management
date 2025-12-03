package com.surest.member.controllers;

import com.surest.member.dto.LoginRequest;
import com.surest.member.dto.RegisterRequest;
import com.surest.member.dto.*;
import com.surest.member.entity.Role;
import com.surest.member.entity.User;
import com.surest.member.exception.GlobalExceptionHandler;
import com.surest.member.exception.UserAlreadyExistsException;
import com.surest.member.repository.RoleRepository;
import com.surest.member.repository.UserRepository;
import com.surest.member.security.JwtUtil;
import com.surest.member.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    private AuthController authController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void login_success_returnsToken() throws Exception {
        LoginRequest req = new LoginRequest("user1", "pass");

        when(userService.validateUserLoginDtls("user1", "pass")).thenReturn("token-abc");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-abc"));

        verify(userService).validateUserLoginDtls("user1", "pass");
    }

    @Test
    void register_usernameExists_returnsConflict() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("exists");
        req.setPassword("p");
        req.setRole("USER");

        when(userService.registerNewUser("exists", "p", "USER"))
                .thenThrow(new UserAlreadyExistsException("User with username exists already exists"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with username exists already exists"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).registerNewUser("exists", "p", "USER");
    }

    @Test
    void register_success_returnsOk() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setPassword("p");
        req.setRole("USER");
        when(userService.registerNewUser("newuser", "p", "USER")).thenReturn(new User());
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(userService).registerNewUser("newuser", "p", "USER");
    }
}
