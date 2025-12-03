package com.surest.member.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidUserExceptionTest {

    @Test
    void testConstructor() {
        InvalidUserException ex = new InvalidUserException("Invalid user");
        assertEquals("Invalid user", ex.getMessage());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Cause");
        InvalidUserException ex = new InvalidUserException("Invalid user", cause);
        assertEquals("Invalid user", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
