package com.surest.member.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginResponseTest {

    @Test
    void testGetterSetter() {
        LoginResponse resp = new LoginResponse();
        resp.setToken("abc123");
        assertEquals("abc123", resp.getToken());
    }
}
