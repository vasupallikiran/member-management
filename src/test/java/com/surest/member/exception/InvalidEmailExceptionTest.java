package com.surest.member.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidEmailExceptionTest {

    @Test
    void testConstructor() {
        InvalidEmailException ex = new InvalidEmailException("Invalid email");
        assertEquals("Invalid email", ex.getMessage());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Cause");
        InvalidEmailException ex = new InvalidEmailException("Invalid email", cause);
        assertEquals("Invalid email", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}