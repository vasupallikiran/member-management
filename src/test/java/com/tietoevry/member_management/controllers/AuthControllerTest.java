package com.tietoevry.member_management.controllers;

import com.tietoevry.member_management.dto.*;
import com.tietoevry.member_management.entity.Role;
import com.tietoevry.member_management.entity.User;
import com.tietoevry.member_management.repository.RoleRepository;
import com.tietoevry.member_management.repository.UserRepository;
import com.tietoevry.member_management.security.JwtUtil;
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

    private AuthController authController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authenticationManager, jwtUtil, userDetailsService, userRepository, roleRepository, passwordEncoder);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_success_returnsToken() throws Exception {
        LoginRequest req = new LoginRequest("user1", "pass");
        UserDetails details = mock(UserDetails.class);

        // authentication manager will not throw => success
        when(userDetailsService.loadUserByUsername(req.getUsername())).thenReturn(details);
        when(jwtUtil.generateToken(details)).thenReturn("token-abc");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-abc"));

        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken(details);
    }

    @Test
    void register_usernameExists_returnsBadRequest() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("exists");
        req.setPassword("p");
        req.setRole("USER");

        when(userRepository.existsByUsername("exists")).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(userRepository).existsByUsername("exists");
        verifyNoInteractions(roleRepository);
    }

    @Test
    void register_success_returnsOk() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setPassword("p");
        req.setRole("USER");

        Role role = new Role();
        role.setName("USER");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("p")).thenReturn("encoded");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(userRepository).save(any(User.class));
    }
}
