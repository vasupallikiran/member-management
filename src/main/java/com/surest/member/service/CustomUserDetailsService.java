package com.surest.member.service;

import com.surest.member.entity.User;
import com.surest.member.exception.InvalidUserException;
import com.surest.member.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws InvalidUserException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUserException("Invalid username or password"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
        );
    }
}
