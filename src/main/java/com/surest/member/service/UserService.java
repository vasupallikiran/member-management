package com.surest.member.service;

import com.surest.member.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    String validateUserLoginDtls(String username, String password);
    User registerNewUser(String username, String password, String role);
}
