package com.tietoevry.member_management.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // e.g., ADMIN or USER
}

