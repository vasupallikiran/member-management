package com.surest.member.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterRequestTest {

    @Test
    void testGetterSetter() {
        RegisterRequest r = new RegisterRequest();
        r.setRole("ADMIN");
        r.setUsername("john");
        r.setPassword("123");

        assertEquals("ADMIN", r.getRole());
        assertEquals("john", r.getUsername());
        assertEquals("123", r.getPassword());
    }
}
