package com.surest.member.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testConstructor() {
        LoginRequest req = new LoginRequest("john", "123");

        assertEquals("john", req.getUsername());
        assertEquals("123", req.getPassword());
    }
}
