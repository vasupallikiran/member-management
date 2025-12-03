package com.surest.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
@Getter
public final class LoginRequest {
    
    @NotBlank(message = "Username is required")
    private final String username;
    
    @NotBlank(message = "Password is required")
    private final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
